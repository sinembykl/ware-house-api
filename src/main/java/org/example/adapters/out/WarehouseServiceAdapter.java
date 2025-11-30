package org.example.adapters.out;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.ports.out.IItemRepository;
import org.example.core.ports.out.IPersistOrderPort;
import org.example.core.ports.out.IReadItemsPort;
import org.example.core.ports.out.IReadOrderPort;
import org.example.core.results.NoContentResult;
import org.example.persistence.ItemEntity;
import org.example.persistence.OrderEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/*
Implements the Outer Port and performs the database save.
 */
@ApplicationScoped
public class WarehouseServiceAdapter implements IItemRepository, IPersistOrderPort,
        IReadItemsPort, IReadOrderPort {

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
    @Transactional
    public NoContentResult persistOrder(Order order){
        try {
            OrderEntity orderEntity = new OrderEntity(order);
            em.persist(orderEntity);
            order.setOrder_id(orderEntity.getOrder_id());

            return new NoContentResult();
        } catch (Exception e){

            NoContentResult noContentResult = new NoContentResult();

            noContentResult.setError(500, e.getMessage());
            return noContentResult;
        }

    }
    @Override
    public List<Item> readItems(){
        try{
            //Define and execute the JPA Query (retrieving Entity objects)
            final var query= em.createQuery("select i from ItemEntity i",ItemEntity.class);
            final List<ItemEntity> itemEntities = query.getResultList();

            //map persistence entities back to domain models
            final List<Item> domainItems = itemEntities.stream().map(this::toDomainModel)
                    .collect(Collectors.toList());

            return domainItems;
        } catch (Exception e){

            throw new RuntimeException("Database read failure for items: " + e.getMessage());
        }

    }
    /*
    Helper method to map ItemEntity to Item Domain Model
     */

    private Item toDomainModel(ItemEntity itemEntity){

        Item item = new Item(itemEntity.getSku(), itemEntity.getItem_name(), itemEntity.getLocation());
        item.setItem_id(itemEntity.getItem_id());
        return item;
    }

    private Order toDomainModel(OrderEntity orderEntity){

        Order order = new Order( orderEntity.getStore(), orderEntity.getUnit());
        order.setOrder_id(orderEntity.getOrder_id());
        return order;
    }

    @Override
    public List<Order> readOrders(int orderId){
        try{
            final var query= em.createQuery("select o from OrderEntity o where o.order_id=order_id",OrderEntity.class);
            final List<OrderEntity> orderEntities = query.getResultList();

            final List<Order> domainOrder = orderEntities.stream().map(this::toDomainModel)
                    .collect(Collectors.toList());

            return domainOrder;
        } catch (Exception e){

            throw new RuntimeException("Database read failure for items: " + e.getMessage());
        }
    }



}
