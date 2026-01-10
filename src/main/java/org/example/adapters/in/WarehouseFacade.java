package org.example.adapters.in;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.core.domain.Employee;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.domain.OrderItem;
import org.example.core.ports.in.*;
import org.example.core.results.NoContentResult;
import org.example.persistence.OrderStatus;

import java.util.List;

@ApplicationScoped
public class WarehouseFacade  {
    /*
    DTO to domain Mapping
     */

    @Inject
    ICreateItemUseCase createItemUseCase;
    @Inject
    ICreateOrderUseCase createOrderUseCase;
    @Inject
    ILoadAllItemUseCase loadAllItemUseCase;
    @Inject
    ILoadOrder loadOrderUseCase;
    @Inject
    ICreateOrderItem createOrderItemUseCase;
    @Inject
    ICreateEmployee createEmployeeUseCase;
    @Inject
    IAssignOrder assignOrder;
    @Inject
    ICompleteOrder completeOrder;


    public NoContentResult createItem(ItemCreationRequest request) {
        Item item = new Item(request.sku,request.name,request.location);

        return  this.createItemUseCase.createItem(item);
    }


    public NoContentResult createOrder(OrderCreationRequest request) {
        Order order = new Order(request.store,request.unit);

        return this.createOrderUseCase.createOrder(order);
    }

    public List<Item> findAllItems() {
        return this.loadAllItemUseCase.loadAllItems();
    }

    public Order findAllOrder(Long id) {
        return this.loadOrderUseCase.loadOrder(id);
    }

    public NoContentResult createOrderItem(OrderItemCreationRequest request) {

        OrderItem orderItem = new OrderItem(request.orderId,request.sku, request.qtyReq);
        return this.createOrderItemUseCase.createOrderItem(orderItem);
    }
    public NoContentResult createEmployee(EmployeeCreationObject request) {
        Employee employee = new Employee(request.name, request.active, request.shift);
        return this.createEmployeeUseCase.createEmployee(employee);
    }
    public NoContentResult assignOrder(Long id, Long employeeId) {
        return this.assignOrder.assignOrder(id, employeeId);
    }
    public NoContentResult completeOrder(Long id, CompletionRequest request) {
        // We pass the data from the Controller directly to the Use Case
        return this.completeOrder.completeOrder(id, request.getStatus());
    }




}















