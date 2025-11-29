package org.example.adapters;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.ports.out.IItemRepository;
import org.example.core.ports.out.IPersistOrderPort;
import org.example.core.results.NoContentResult;
import org.example.persistence.ItemEntity;
import org.example.persistence.OrderEntity;

/*
Implements the Outer Port and performs the database save.
 */
@ApplicationScoped
public class WarehouseServiceAdapter implements IItemRepository, IPersistOrderPort {

    @Inject
    EntityManager em;


    @Override
    @Transactional// Quarkus automatically handles transaction begin/commit/rollback
    public Item createItem(Item item) {

        // Translate Domain Model to Entity
        ItemEntity itemEntity = new ItemEntity(item);

        //JPA Operation
        em.persist(itemEntity);

        // Get the db-generated ID and update the core Domain Model
        item.setItem_id(itemEntity.getItem_id());



        return item;
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



}
