package org.example.core.logic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.ports.in.ICreateOrderUseCase;
import org.example.core.ports.in.ILoadAllItemUseCase;
import org.example.core.ports.in.ILoadOrderUseCase;
import org.example.core.ports.out.IItemRepository;
import org.example.core.ports.in.ICreateItemUseCase;
import org.example.core.ports.out.IPersistOrderPort;
import org.example.core.ports.out.IReadItemsPort;
import org.example.core.ports.out.IReadOrderPort;
import org.example.core.results.NoContentResult;

import java.util.List;


/*
We are implementing Inner Port and accessing to Outer Port
 */
@ApplicationScoped // Necessary for Quarkus to manage and inject this class
public class WarehouseService implements ICreateItemUseCase, ICreateOrderUseCase, ILoadAllItemUseCase, ILoadOrderUseCase {
    /*
    The @ApplicationScoped and @Inject annotations allow Quarkus to manage the instance of ItemManager
    and provide it with the necessary dependency (ItemService) when the API calls the system.

     */
    private IItemRepository itemRepository;
    private IPersistOrderPort persistOrderPort;
    private IReadItemsPort readItemsPort;
    private IReadOrderPort readOrderPort;

    @Inject
    public WarehouseService(IItemRepository itemRepository, IPersistOrderPort persistOrderPort,
                            IReadItemsPort readItemsPort, IReadOrderPort readOrderPort) {
        this.itemRepository = itemRepository;
        this.persistOrderPort = persistOrderPort;
        this.readItemsPort = readItemsPort;
        this.readOrderPort = readOrderPort;
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

    @Override
    public List<Item> loadAllItems(){
        //inner port overwritten, now it is calling outer port
        return this.readItemsPort.readItems();
    }

    @Override
    public List<Order> loadOrders(int orderId){

        return this.readOrderPort.readOrders(orderId);
    }
    @Override
    public boolean existsBySku(String sku){
        return this.itemRepository.existsBySku(sku);
    }
}
