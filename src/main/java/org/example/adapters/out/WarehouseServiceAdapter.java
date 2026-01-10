package org.example.adapters.out;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.example.core.domain.Employee;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.domain.OrderItem;
import org.example.core.ports.in.ICompleteOrder;
import org.example.core.ports.in.ICreateOrderItem;
import org.example.core.ports.out.*;
import org.example.core.results.NoContentResult;
import org.example.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/*
Implements the Outer Port and performs the database save.
 */
@ApplicationScoped
public class WarehouseServiceAdapter implements IItemRepository, IPersistOrderPort,
        IReadItemsPort, IReadOrderPort, IOrderItemRepository, IPersistEmployeePort, IAssignOrderOutPort, ICompleteOrderOutPort{

    @Inject
    EntityManager em;


    @Override
    @Transactional// Quarkus automatically handles transaction begin/commit/rollback
    public NoContentResult createItem(Item item) {
        try {

            // map domain model to entity
            ItemEntity itemEntity = new ItemEntity(item);
            //JPA Operation
            em.persist(itemEntity);
            // Get the db-generated ID and update the core Domain Model
            item.setItem_id(itemEntity.getItem_id());
            return new NoContentResult();

        } catch (Exception e){
            NoContentResult noContentResult = new NoContentResult();
            noContentResult.setError(500,  e.getMessage());
            return noContentResult;
        }


    }

    @Override
    public boolean existsBySku(String sku) {

        return !em.createQuery("select i from ItemEntity i where i.sku= :sku", ItemEntity.class)
                .setParameter("sku", sku)
                .setMaxResults(1)
                .getResultList()
                .isEmpty();

    }

    @Override
    @Transactional
    public NoContentResult persistOrder(Order order){
        try {
            OrderEntity orderEntity = new OrderEntity(order);
            em.persist(orderEntity);
            order.setOrder_id(orderEntity.getOrder_id());
            NoContentResult noContentResult = new NoContentResult();
            noContentResult.setId(orderEntity.getOrder_id());

            return noContentResult;
        } catch (Exception e){

            NoContentResult noContentResult = new NoContentResult();

            noContentResult.setError(500, e.getMessage());
            return noContentResult;
        }

    }
    @Override
    public List<Item> readItems() {
        return em.createQuery("select i from ItemEntity i", ItemEntity.class)
                .getResultStream()
                .map(WarehouseMapper::toDomain) // Use the Static Mapper
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Order readOrder(Long orderId) { // Change return type to Order
        try {
            List<OrderEntity> entities = em.createQuery(
                            "select o from OrderEntity o where o.order_id = :id", OrderEntity.class)
                    .setParameter("id", orderId)
                    .getResultList();

            // Check if list is empty to avoid index errors
            if (entities.isEmpty()) {
                return null;
            }

            // Map only the first result and return a single Order
            return WarehouseMapper.toDomain(entities.get(0));

        } catch (Exception e) {
            throw new RuntimeException("Database read failure: " + e.getMessage());
        }
    }
    @Override
    @Transactional
    public NoContentResult createOrderItem(OrderItem orderItem) {
        try {
            // 1. Find the Parent OrderEntity by ID
            // We need this to tell JPA which Order this item belongs to
            OrderEntity orderEntity = em.find(OrderEntity.class, orderItem.getOrderId());

            if (orderEntity == null) {
                NoContentResult error = new NoContentResult();
                error.setError(404, "Database Error: Order with ID " + orderItem.getOrderId() + " not found.");
                return error;
            }

            // 2. Find the ItemEntity by SKU
            // We use a query because SKU is a string, not the Primary Key (ID)
            ItemEntity itemEntity = em.createQuery("select i from ItemEntity i where i.sku = :sku", ItemEntity.class)
                    .setParameter("sku", orderItem.getItem_sku())
                    .getSingleResult();

            // 3. Create the Persistence Entity and LINK them
            OrderItemEntity entity = new OrderItemEntity();

            // This is the "Linking" part
            entity.setOrder(orderEntity); // Connects to the Order row
            entity.setItem(itemEntity);   // Connects to the Item row

            // Set values from Domain
            entity.setQtyRequired(orderItem.getQtyRequired());
            entity.setQtyPicked(0); // Rule: Picking always starts at 0 for new items
            entity.setLocation(itemEntity.getLocation()); // Use the warehouse location from the Item

            // 4. Save to Database
            em.persist(entity);

            NoContentResult result = new NoContentResult();
            result.setId(entity.getId());
            return result;

        } catch (jakarta.persistence.NoResultException e) {
            NoContentResult error = new NoContentResult();
            error.setError(404, "Item SKU " + orderItem.getItem_sku() + " does not exist.");
            return error;
        } catch (Exception e) {
            NoContentResult error = new NoContentResult();
            error.setError(500, "Persistence failed: " + e.getMessage());
            return error;
        }
    }

    @Override
    @Transactional
    public NoContentResult persistEmployee(Employee employee){
        try{
            EmployeeEntity employeeEntity = new EmployeeEntity(employee);
            em.persist(employeeEntity);
            employee.setId(employee.getId());
            NoContentResult noContentResult = new NoContentResult();
            noContentResult.setId(employee.getId());
            return noContentResult;
        } catch (Exception e) {
            NoContentResult noContentResult = new NoContentResult();
            noContentResult.setError(500,  e.getMessage());
            return noContentResult;
        }

    }

    @Override
    @Transactional
    public NoContentResult updateOrder(Long id, Long EmployeeId) {

        try {
            EmployeeEntity employeeEntity = em.find(EmployeeEntity.class, EmployeeId);
            OrderEntity orderEntity = em.find(OrderEntity.class, id);

            if(orderEntity != null && employeeEntity != null){

                orderEntity.setEmployee(employeeEntity);
                //UC-04
                orderEntity.setStatus(OrderStatus.IN_PROGRESS);
                return new NoContentResult();

            } else {
                NoContentResult error = new NoContentResult();
                error.setError(404, "Order or Employee not found");
                return error;
            }

        } catch (Exception e){
            throw new RuntimeException("Failed tp assign employee: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public NoContentResult completeOrder(Long id, OrderStatus finalStatus) {
        try {
            // Find the Entity by ID
            OrderEntity entity = em.find(OrderEntity.class, id);

            if (entity == null) {
                return new NoContentResult(404, "Order Entity not found in database.");
            }

            // Apply the final state
            entity.setStatus(finalStatus);

            // Prepare the response
            NoContentResult result = new NoContentResult();
            result.setId(entity.getOrder_id());
            return result;

        } catch (Exception e) {
            return new NoContentResult(500, "Database update failed: " + e.getMessage());
        }
    }

    }





