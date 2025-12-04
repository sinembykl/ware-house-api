package org.example.adapters.in;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.ports.in.ICreateItemUseCase;
import org.example.core.ports.in.ICreateOrderUseCase;
import org.example.core.ports.in.ILoadAllItemUseCase;
import org.example.core.ports.in.ILoadOrderUseCase;
import org.example.core.ports.out.IPersistOrderPort;

import java.util.List;

@ApplicationScoped
public class WarehouseFacade  {

    @Inject
    ICreateItemUseCase createItemUseCase;
    @Inject
    ICreateOrderUseCase createOrderUseCase;
    @Inject
    ILoadAllItemUseCase loadAllItemUseCase;
    @Inject
    ILoadOrderUseCase loadOrderUseCase;

    public void createItem(ItemCreationRequest request) {
        Item item = new Item(request.sku,request.name,request.location);

        if (createItemUseCase.existsBySku(request.sku)) {
            throw new RuntimeException("Sku is already in use");
        }
        this.createItemUseCase.createItem(item);
    }


    public void createOrder(OrderCreationRequest request) {
        Order order = new Order(request.store,request.unit);
        this.createOrderUseCase.createOrder(order);
    }

    public List<Item> findAllItems() {
        return this.loadAllItemUseCase.loadAllItems();
    }




}















