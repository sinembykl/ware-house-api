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
public class WarehouseFacade {

    @Inject
    WarehouseService warehouseService;

    // --- ITEM OPERATIONS ---

    public NoContentResult createItem(ItemCreationRequest request) {
        // Explicitly mapping DTO to Domain
        Item item = new Item(request.sku, request.name, request.location);
        return this.warehouseService.createItem(item);
    }

    public Item loadItem(String sku) {
        // Pass-through to service; if this returns null, Controller handles 404
        return this.warehouseService.loadItem(sku);
    }

    public List<Item> findItems(String location, int limit, int offset) {
        return warehouseService.findItems(location, limit, offset);
    }

    public List<Item> findAllItems() {
        return this.warehouseService.loadAllItems();
    }

    public NoContentResult updateItem(String sku, ItemCreationRequest request) {
        Item item = new Item(sku, request.name, request.location);
        return warehouseService.updateItem(sku, item);
    }

    public NoContentResult deleteItem(String sku) {
        return warehouseService.deleteItem(sku);
    }

    // --- ORDER OPERATIONS ---

    public NoContentResult createOrder(OrderCreationRequest request) {
        Order order = new Order(request.store, request.unit);
        return this.warehouseService.createOrder(order);
    }

    public Order findAllOrder(Long id) {
        return this.warehouseService.loadOrder(id);
    }

    public NoContentResult updateOrder(Long id, OrderCreationRequest request) {
        Order order = new Order(request.store, request.unit);
        return this.warehouseService.updateOrder(id, order);
    }

    public NoContentResult deleteOrder(Long id) {
        return warehouseService.deleteOrder(id);
    }

    public NoContentResult completeOrder(Long id, CompletionRequest request) {
        // Ensure status is pulled correctly from the DTO
        return this.warehouseService.completeOrder(id, request.getStatus());
    }

    public NoContentResult assignOrder(Long id, Long employeeId) {
        return this.warehouseService.assignOrder(id, employeeId);
    }

    // --- ORDER ITEM OPERATIONS ---

    public NoContentResult createOrderItem(Long orderId, OrderItemCreationRequest request) {
        OrderItem orderItem = new OrderItem(orderId, request.sku, request.qtyReq);
        return this.warehouseService.createOrderItem(orderItem);
    }

    public OrderItem findById(Long id) {
        return this.warehouseService.findById(id);
    }

    public NoContentResult pickOrderItem(Long id, OrderItemPickRequest request) {
        return this.warehouseService.pickOrderItem(id, request.getAmount());
    }

    public NoContentResult updateOrderItem(Long id, OrderItemCreationRequest request) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQtyRequired(request.qtyReq);
        return warehouseService.updateOrderItem(id, orderItem);
    }

    public NoContentResult deleteOrderItem(Long id) {
        return warehouseService.deleteOrderItem(id);
    }

    // --- EMPLOYEE OPERATIONS ---

    public NoContentResult createEmployee(EmployeeCreationObject request) {
        Employee employee = new Employee(request.name, request.active, request.shift);
        return this.warehouseService.createEmployee(employee);
    }

    public Employee findEmployeeById(Long id) {
        return this.warehouseService.getEmployee(id);
    }

    public NoContentResult updateEmployee(Long id, EmployeeCreationObject request) {
        Employee employee = new Employee(request.name, request.active, request.shift);
        return warehouseService.updateEmployee(id, employee);
    }

    public NoContentResult deleteEmployee(Long id) {
        return warehouseService.deleteEmployee(id);
    }
}








