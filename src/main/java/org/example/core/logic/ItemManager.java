package org.example.core.logic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.core.domain.Item;
import org.example.core.ports.IItemRepository;
import org.example.core.ports.IItemUseCase;


/*
We are implementing Inner Port and accessing to Outer Port
 */
@ApplicationScoped // Necessary for Quarkus to manage and inject this class
public class ItemManager implements IItemUseCase {
    /*
    The @ApplicationScoped and @Inject annotations allow Quarkus to manage the instance of ItemManager
    and provide it with the necessary dependency (ItemService) when the API calls the system.

     */
    private IItemRepository itemRepository;

    @Inject
    public ItemManager(IItemRepository itemRepository){
        this.itemRepository = itemRepository;
    }



    @Override
    public Item createItem(int sku, String item_name, String location) {

        Item item = new Item(sku, item_name, location);

        return itemRepository.createItem(item);
    }
}
