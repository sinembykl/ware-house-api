package org.example.core.logic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.domain.OrderItem;
import org.example.core.ports.in.*;
import org.example.core.ports.out.*;
import org.example.core.results.NoContentResult;
import org.example.persistence.OrderStatus;

import java.util.List;


/*
We are implementing Inner Port and accessing to Outer Port
 */
@ApplicationScoped // Necessary for Quarkus to manage and inject this class
public class WarehouseService implements ICreateItemUseCase, ICreateOrderUseCase, ILoadAllItemUseCase, ILoadOrderUseCase, ICreateOrderItem {
    /*
    The @ApplicationScoped and @Inject annotations allow Quarkus to manage the instance of ItemManager
    and provide it with the necessary dependency (ItemService) when the API calls the system.

     */
    private IItemRepository itemRepository;
    private IPersistOrderPort persistOrderPort;
    private IReadItemsPort readItemsPort;
    private IReadOrderPort readOrderPort;
    private IOrderItemRepository orderItemRepository;

    @Inject
    public WarehouseService(IItemRepository itemRepository, IPersistOrderPort persistOrderPort,
                            IReadItemsPort readItemsPort, IReadOrderPort readOrderPort, IOrderItemRepository orderItemRepository) {
        this.itemRepository = itemRepository;
        this.persistOrderPort = persistOrderPort;
        this.readItemsPort = readItemsPort;
        this.readOrderPort = readOrderPort;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public NoContentResult createItem(Item item) {
        if (itemRepository.existsBySku(item.getSku())) {
            NoContentResult result = new NoContentResult();
            result.setError(400,"SKU is already in use" );
            return result;
        }
        //calling outer port
        return this.itemRepository.createItem(item);
    }
    @Override
    public NoContentResult createOrder(Order order){
        NoContentResult result = this.persistOrderPort.persistOrder(order);

        if(!result.hasError()){
            System.out.println("Order has been successfully created" + order.getStatus());
        }

        return result;
    }

    @Override
    public List<Item> loadAllItems(){
        //inner port overwritten, now it is calling outer port
        return this.readItemsPort.readItems();
    }

    @Override
    public List<Order> loadOrders(Long orderId){

        return this.readOrderPort.readOrders(orderId);
    }
    @Override
    public boolean existsBySku(String sku){
        return this.itemRepository.existsBySku(sku);
    }

    @Override
    public NoContentResult createOrderItem(OrderItem orderItem) {
        // 1. Fetch the existing order
        List<Order> orders = this.readOrderPort.readOrders(orderItem.getOrderId());

        if (orders.isEmpty()) {
            NoContentResult error = new NoContentResult();
            error.setError(404, "Order not found");
            return error;
        }

        Order existingOrder = orders.get(0);

        // 2. APPLY PRECONDITION: Order must be in CREATED state [cite: 86]
        if (existingOrder.getStatus() != OrderStatus.CREATED) {
            NoContentResult error = new NoContentResult();
            // UC-03 Failure Requirement: Invalid state -> 409 Conflict [cite: 88]
            error.setError(409, "Precondition Failed: Order is in state " + existingOrder.getStatus());
            return error;
        }

        // 3. If check passes, proceed to save [cite: 87, 89]
        return this.orderItemRepository.createOrderItem(orderItem);
    }
}
