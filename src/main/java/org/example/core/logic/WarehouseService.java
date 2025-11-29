package org.example.core.logic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.ports.in.IOrderUseCase;
import org.example.core.ports.out.IItemRepository;
import org.example.core.ports.in.IItemUseCase;
import org.example.core.ports.out.IPersistOrderPort;
import org.example.core.results.NoContentResult;


/*
We are implementing Inner Port and accessing to Outer Port
 */
@ApplicationScoped // Necessary for Quarkus to manage and inject this class
public class WarehouseService implements IItemUseCase, IOrderUseCase {
    /*
    The @ApplicationScoped and @Inject annotations allow Quarkus to manage the instance of ItemManager
    and provide it with the necessary dependency (ItemService) when the API calls the system.

     */
    private IItemRepository itemRepository;
    private IPersistOrderPort persistOrderPort;

    @Inject
    public WarehouseService(IItemRepository itemRepository){
        this.itemRepository = itemRepository;
    }



    @Override
    public Item createItem(int sku, String item_name, String location) {

        Item item = new Item(sku, item_name, location);

        return itemRepository.createItem(item);
    }
    @Override
    public NoContentResult createOrder(Order order){
        // any business logic or validation specific to order creation will be here

        // connection to the Outer Port
        return this.persistOrderPort.persistOrder(order);
    }
}
