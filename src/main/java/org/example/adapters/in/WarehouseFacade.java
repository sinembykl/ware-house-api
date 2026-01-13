package org.example.adapters.in;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.core.domain.Employee;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.domain.OrderItem;
import org.example.core.logic.WarehouseService;
import org.example.core.results.NoContentResult;

import java.util.List;

@ApplicationScoped
public class WarehouseFacade  {
    /*
    DTO to domain Mapping
     */

    @Inject
    WarehouseService warehouseService;

    public NoContentResult createItem(ItemCreationRequest request) {
        Item item = new Item(request.sku,request.name,request.location);

        return  this.warehouseService.createItem(item);
    }


    public NoContentResult createOrder(OrderCreationRequest request) {
        Order order = new Order(request.store,request.unit);

        return this.warehouseService.createOrder(order);
    }

    public List<Item> findAllItems() {
        return this.warehouseService.loadAllItems();
    }

    public Order findAllOrder(Long id) {
        return this.warehouseService.loadOrder(id);
    }

    public NoContentResult createOrderItem(Long orderId, OrderItemCreationRequest request) {

        OrderItem orderItem = new OrderItem(orderId,request.sku, request.qtyReq);
        return this.warehouseService.createOrderItem(orderItem);
    }
    public NoContentResult createEmployee(EmployeeCreationObject request) {
        Employee employee = new Employee(request.name, request.active, request.shift);
        return this.warehouseService.createEmployee(employee);
    }
    public NoContentResult assignOrder(Long id, Long employeeId) {
        return this.warehouseService.assignOrder(id, employeeId);
    }
    public NoContentResult completeOrder(Long id, CompletionRequest request) {
        // We pass the data from the Controller directly to the Use Case
        return this.warehouseService.completeOrder(id, request.getStatus());
    }
    public NoContentResult pickOrderItem(Long id, OrderItemPickRequest request) {
        return this.warehouseService.pickOrderItem(id, request.getAmount());
        //
    }

    public OrderItem findById(Long id){
        return this.warehouseService.findById(id);
    }

    public NoContentResult deleteItem(String sku) {
        return warehouseService.deleteItem(sku);
    }

    public NoContentResult deleteOrder(Long id) {
        return warehouseService.deleteOrder(id);
    }

    public NoContentResult deleteEmployee(Long id) {
        return warehouseService.deleteEmployee(id);
    }

    public NoContentResult deleteOrderItem(Long id) {
        return warehouseService.deleteOrderItem(id);
    }
    public Item loadItem(String sku) {
        return this.warehouseService.loadItem(sku);
    }

    public NoContentResult updateItem(String sku, ItemCreationRequest request) {
        Item item = new Item(sku, request.name, request.location);
        return warehouseService.updateItem(sku, item);
    }
    public NoContentResult updateEmployee(Long id, EmployeeCreationObject request) {
        Employee employee = new Employee(request.name, request.active, request.shift);
        return warehouseService.updateEmployee(id, employee);
    }

    public NoContentResult updateOrderItem(Long id, OrderItemCreationRequest request) {
        // Assuming you have an appropriate DTO or logic for updates
        OrderItem orderItem = new OrderItem();
        orderItem.setQtyRequired(request.qtyReq);
        return warehouseService.updateOrderItem(id, orderItem);
    }
    public NoContentResult updateOrder(Long id, OrderCreationRequest request) {
        // 1. Map DTO to Domain
        // Note: Assuming your Order constructor or setters accept store and unit
        Order order = new Order(request.store, request.unit);

        // 2. Delegate to the Inner Port (Inbound Port)
        return this.warehouseService.updateOrder(id, order);
    }
    public Employee findEmployeeById(Long id) {
        return this.warehouseService.getEmployee(id);
    }


}















