package org.example.adapters.out;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.example.core.domain.*;
import org.example.core.results.NoContentResult;
import org.example.persistence.*;
import org.example.ports.out.*;
import org.example.core.domain.OrderStatus;


import java.util.List;
import java.util.stream.Collectors;

/*
Implements the Outer Port and performs the database save.
 */
@ApplicationScoped
public class WarehouseServiceAdapter implements IItemRepository, IPersistOrderPort,
        IReadItemPort, IReadOrderPort, IOrderItemRepository, IPersistEmployeePort, IAssignOrderOutPort, ICompleteOrderOutPort, IOrderItemPickOutPort,IDeleteEntityOutPort, IUpdateEntityOutPort {

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

        } catch (Exception e) {
            NoContentResult noContentResult = new NoContentResult();
            noContentResult.setError(500, e.getMessage());
            return noContentResult;
        }


    }

    @Override
    @Transactional
    public boolean existsBySku(String sku) {

        return !em.createQuery("select i from ItemEntity i where i.sku= :sku", ItemEntity.class)
                .setParameter("sku", sku)
                .setMaxResults(1)
                .getResultList()
                .isEmpty();

    }

    @Override
    @Transactional
    public NoContentResult persistOrder(Order order) {
        try {
            OrderEntity orderEntity = new OrderEntity(order);
            em.persist(orderEntity);
            order.setOrder_id(orderEntity.getOrder_id());
            NoContentResult noContentResult = new NoContentResult();
            noContentResult.setId(orderEntity.getOrder_id());

            return noContentResult;
        } catch (Exception e) {

            NoContentResult noContentResult = new NoContentResult();

            noContentResult.setError(500, e.getMessage());
            return noContentResult;
        }

    }
    @Override
    @Transactional
    public List<Item> readItems(String location, int limit, int offset) {
        StringBuilder queryBuilder = new StringBuilder("select i from ItemEntity i"); //

        if (location != null && !location.isBlank()) {
            queryBuilder.append(" where i.location = :location"); //
        }

        var q = em.createQuery(queryBuilder.toString(), ItemEntity.class);

        if (location != null && !location.isBlank()) {
            q.setParameter("location", location);
        }

        return q.setFirstResult(offset)
                .setMaxResults(limit)
                .getResultStream()
                .map(WarehouseMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Item> readItems() {
        return em.createQuery("select i from ItemEntity i", ItemEntity.class)
                .getResultStream()
                .map(WarehouseMapper::toDomain) // Use the Static Mapper
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Order readOrder(Long orderId) {
        try {
            // FIX: Add "left join fetch o.orderItemEntities" to the query
            List<OrderEntity> entities = em.createQuery(
                            "select o from OrderEntity o " +
                                    "left join fetch o.orderItemEntities " +
                                    "where o.order_id = :id", OrderEntity.class)
                    .setParameter("id", orderId)
                    .getResultList();

            if (entities.isEmpty()) {
                return null;
            }

            // Now the entities list contains the items, and the mapper will see them
            return WarehouseMapper.toDomain(entities.get(0));

        } catch (Exception e) {
            throw new RuntimeException("Database read failure: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public NoContentResult saveOrderItem(OrderItem orderItem) {
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
            orderEntity.getOrderItemEntities().add(entity);

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
    public NoContentResult persistEmployee(Employee employee) {
        try {
            EmployeeEntity employeeEntity = new EmployeeEntity(employee);
            em.persist(employeeEntity);
            employee.setId(employeeEntity.getId());
            NoContentResult noContentResult = new NoContentResult();
            noContentResult.setId(employeeEntity.getId());
            return noContentResult;
        } catch (Exception e) {
            NoContentResult noContentResult = new NoContentResult();
            noContentResult.setError(500, e.getMessage());
            return noContentResult;
        }

    }

    @Override
    @Transactional
    public NoContentResult updateOrder(Long id, Long EmployeeId) {

        try {
            EmployeeEntity employeeEntity = em.find(EmployeeEntity.class, EmployeeId);
            OrderEntity orderEntity = em.find(OrderEntity.class, id);

            if (orderEntity != null && employeeEntity != null) {

                orderEntity.setEmployee(employeeEntity);
                //UC-04
                orderEntity.setStatus(OrderStatus.IN_PROGRESS);
                return new NoContentResult();

            } else {
                NoContentResult error = new NoContentResult();
                error.setError(404, "Order or Employee not found");
                return error;
            }

        } catch (Exception e) {
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

    @Override
    @Transactional
    public OrderItem findById(Long id) {
        try {
            // 1. Find the Entity in the database
            OrderItemEntity entity = em.find(OrderItemEntity.class, id);

            if (entity == null) {
                return null;
            }

            // 2. Use your Mapper to turn it into a Domain object
            return WarehouseMapper.toDomain(entity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find Order Item: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public NoContentResult pickOrderItem(Long orderItemId, int amount) {

        try {
            OrderItemEntity entity = em.find(OrderItemEntity.class, orderItemId);
            if (entity == null) {
                return null;
            }

            int newTotal = entity.getQtyPicked() + amount;
            entity.setQtyPicked(newTotal);
            NoContentResult result = new NoContentResult();
            result.setId(entity.getId());
            return result;
        } catch (Exception e) {
            NoContentResult error = new NoContentResult();
            error.setError(500, "Update failed: " + e.getMessage());
            return error;
        }
    }
    @Override
    @Transactional
    public void deleteOrderEntity(Long orderId) {
        OrderEntity entity = em.find(OrderEntity.class, orderId);
        if (entity != null) em.remove(entity);
    }

    @Override
    @Transactional
    public void deleteItemEntity(String sku) {
        // Query required if SKU is not the primary key
        em.createQuery("DELETE FROM ItemEntity i WHERE i.sku = :sku")
                .setParameter("sku", sku)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void deleteEmployeeEntity(Long employeeId) {
        EmployeeEntity entity = em.find(EmployeeEntity.class, employeeId);
        if (entity != null) em.remove(entity);
    }

    @Override
    @Transactional
    public void deleteOrderItemEntity(Long orderItemId) {
        OrderItemEntity entity = em.find(OrderItemEntity.class, orderItemId);
        if (entity != null) em.remove(entity);
    }

    @Override
    @Transactional
    public Item readItemBySku(String sku) {
        try {
            // Query the database for the ItemEntity
            ItemEntity entity = em.createQuery(
                            "SELECT i FROM ItemEntity i WHERE i.sku = :sku", ItemEntity.class)
                    .setParameter("sku", sku)
                    .getSingleResult();

            // Convert the Entity to Domain using your mapper
            return WarehouseMapper.toDomain(entity);
        } catch (NoResultException e) {
            return null; // Return null if the SKU is not found
        }
    }
    @Override
    @Transactional
    public void updateItemEntity(String sku, Item item) {
        // 1. Retrieve the MANAGED entity
        ItemEntity entity = em.createQuery("SELECT i FROM ItemEntity i WHERE i.sku = :sku", ItemEntity.class)
                .setParameter("sku", sku)
                .getSingleResult();

        if (entity != null) {
            // 2. Set the values
            entity.setItem_name(item.getName());
            entity.setLocation(item.getLocation());

            // 3. FORCE the SQL UPDATE command to run immediately
            // This ensures MariaDB receives the data before the response is sent.
            em.flush();
        }
    }

    @Override
    @Transactional
    public void updateOrderEntity(Long id, Order order) {
        OrderEntity entity = em.find(OrderEntity.class, id);
        if (entity != null) {
            entity.setStore(order.getStore());
            // Map other fields as necessary
        }
    }

    @Override
    @Transactional
    public void updateEmployeeEntity(Long id, Employee employee) {
        EmployeeEntity entity = em.find(EmployeeEntity.class, id);
        if (entity != null) {
            entity.setName(employee.getName());
            if (employee.isActive()) {
                entity.activate();
            } else {
                entity.deactivate();
            }
            entity.setShift(employee.getShift());
            em.flush(); // Force the change to MariaDB
        }
    }

    @Override
    @Transactional
    public void updateOrderItemEntity(Long id, OrderItem orderItem) {
        OrderItemEntity entity = em.find(OrderItemEntity.class, id);
        if (entity != null) {
            entity.setQtyRequired(orderItem.getQtyRequired());
            entity.setQtyPicked(orderItem.getQtyPicked());
        }
    }
    @Override
    @Transactional
    public Employee readEmployee(Long employeeId) {
        try {
            // 1. Find the Entity in MariaDB
            EmployeeEntity entity = em.find(EmployeeEntity.class, employeeId);

            // ADD THIS DEBUG OUTPUT
            System.out.println("=== DEBUG ===");
            System.out.println("Found entity: " + (entity != null));
            if (entity != null) {
                System.out.println("Entity ID: " + entity.getId());
                System.out.println("Entity Name: " + entity.getName());
                System.out.println("Entity Active: " + entity.isActive());
                System.out.println("Entity Shift: " + entity.getShift());
            }
            System.out.println("=============");

            if (entity == null) {
                return null;
            }

            // 2. Map Entity to Domain
            Employee employee = WarehouseMapper.toDomain(entity);

            // ADD THIS DEBUG OUTPUT TOO
            System.out.println("=== MAPPED EMPLOYEE ===");
            System.out.println("Employee ID: " + employee.getId());
            System.out.println("Employee Name: " + employee.getName());
            System.out.println("Employee Active: " + employee.isActive());
            System.out.println("Employee Shift: " + employee.getShift());
            System.out.println("======================");

            return employee;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read employee: " + e.getMessage());
        }
    }

}





