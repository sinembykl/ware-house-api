package org.example.adapters;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.example.core.domain.Item;
import org.example.core.ports.IItemRepository;
import org.example.persistence.ItemEntity;

/*
Implements the Outer Port and performs the database save.
 */
@ApplicationScoped
public class ItemService implements IItemRepository {

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



}
