package org.example.core.logic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.ports.in.ICreateOrderUseCase;
import org.example.core.ports.out.IItemRepository;
import org.example.core.ports.in.ICreateItemUseCase;
import org.example.core.ports.out.IPersistOrderPort;
import org.example.core.results.NoContentResult;


/*
We are implementing Inner Port and accessing to Outer Port
 */
@ApplicationScoped // Necessary for Quarkus to manage and inject this class
public class WarehouseService implements ICreateItemUseCase, ICreateOrderUseCase {
    /*
    The @ApplicationScoped and @Inject annotations allow Quarkus to manage the instance of ItemManager
    and provide it with the necessary dependency (ItemService) when the API calls the system.

     */
    private IItemRepository itemRepository;
    private IPersistOrderPort persistOrderPort;

    @Inject
    public WarehouseService(IItemRepository itemRepository, IPersistOrderPort persistOrderPort) {
        this.itemRepository = itemRepository;
        this.persistOrderPort = persistOrderPort;
    }



    @Override
    public NoContentResult createItem(Item item) {


        //calling outer port
        return this.itemRepository.createItem(item);
    }
    @Override
    public NoContentResult createOrder(Order order){
        // any business logic or validation specific to order creation will be here

        // connection to the Outer Port
        return this.persistOrderPort.persistOrder(order);
    }
}
