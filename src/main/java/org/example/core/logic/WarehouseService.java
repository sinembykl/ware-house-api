package org.example.core.logic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.core.domain.Employee;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.domain.OrderItem;
import org.example.core.results.NoContentResult;
import org.example.core.domain.OrderStatus;
import org.example.ports.in.*;
import org.example.ports.out.*;

import io.quarkus.cache.CacheResult;
import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;


import java.util.List;


/*
We are implementing Inner Port and accessing to Outer Port
 */
@ApplicationScoped // Necessary for Quarkus to manage and inject this class
public class WarehouseService implements ICreateItemUseCase, ICreateOrderUseCase, ILoadItemUseCase, ILoadOrder, ICreateOrderItem, ICreateGetEmployee, IAssignOrder, ICompleteOrder, IOrderItemPickUseCase, IDeleteDomainUseCase, IPutDomainUseCase {
    /*
    The @ApplicationScoped and @Inject annotations allow Quarkus to manage the instance of ItemManager
    and provide it with the necessary dependency (ItemService) when the API calls the system.

     */
    private IItemRepository itemRepository;
    private IPersistOrderPort persistOrderPort;
    private IReadItemPort readItemsPort;
    private IReadOrderPort readOrderPort;
    private IOrderItemRepository orderItemRepository;
    private IPersistEmployeePort persistEmployeePort;
    private IAssignOrderOutPort assignOrderOutPort;
    private ICompleteOrderOutPort completeOrderOutPort;
    private IOrderItemPickOutPort orderItemPickOutPort;
    private IDeleteEntityOutPort deleteEntityOutPort;
    private IUpdateEntityOutPort updateEntityOutPort;
    @Inject
    public WarehouseService(IItemRepository itemRepository, IPersistOrderPort persistOrderPort,
                            IReadItemPort readItemsPort, IReadOrderPort readOrderPort, IOrderItemRepository orderItemRepository, IPersistEmployeePort persistEmployeePort, IAssignOrderOutPort assignOrderOutPort, ICompleteOrderOutPort completeOrderOutPort, IOrderItemPickOutPort orderItemPickOutPort, IDeleteEntityOutPort deleteEntityOutPort, IUpdateEntityOutPort updateEntityOutPort) {
        this.itemRepository = itemRepository;
        this.persistOrderPort = persistOrderPort;
        this.readItemsPort = readItemsPort;
        this.readOrderPort = readOrderPort;
        this.orderItemRepository = orderItemRepository;
        this.persistEmployeePort = persistEmployeePort;
        this.assignOrderOutPort = assignOrderOutPort;
        this.completeOrderOutPort = completeOrderOutPort;
        this.orderItemPickOutPort = orderItemPickOutPort;
        this.deleteEntityOutPort = deleteEntityOutPort;
        this.updateEntityOutPort = updateEntityOutPort;
    }


    @CacheResult(cacheName = "items")
    @Override
    public boolean existsBySku(@CacheKey String sku) {
        return this.itemRepository.existsBySku(sku);
    }

    @CacheResult(cacheName = "items")
    @Override
    public Item loadItem(@CacheKey String sku) {
        if (sku == null || sku.isEmpty()) {
            return null;
        }
        return this.readItemsPort.readItemBySku(sku);
    }

    @CacheResult(cacheName = "items")
    @Override
    public List<Item> loadAllItems() {
        return this.readItemsPort.readItems();
    }

    @CacheResult(cacheName = "orders")
    @Override
    public Order loadOrder(@CacheKey Long orderId) {
        return this.readOrderPort.readOrder(orderId);
    }

    @CacheResult(cacheName = "employees")
    @Override
    public Employee getEmployee(@CacheKey Long employeeId) {
        return this.persistEmployeePort.readEmployee(employeeId);
    }

    @CacheResult(cacheName = "orderItems")
    @Override
    public OrderItem findById(@CacheKey Long id) {
        return this.orderItemRepository.findById(id);
    }

    // ============================================
    // WRITE OPERATIONS WITH CACHE INVALIDATION
    // ============================================

    @CacheInvalidate(cacheName = "items")
    @Override
    public NoContentResult createItem(Item item) {
        if (itemRepository.existsBySku(item.getSku())) {
            NoContentResult result = new NoContentResult();
            result.setError(400, "SKU is already in use");
            return result;
        }
        return this.itemRepository.createItem(item);
    }

    @CacheInvalidate(cacheName = "items")
    @Override
    public NoContentResult updateItem(@CacheKey String sku, Item item) {
        if (!itemRepository.existsBySku(sku)) {
            return new NoContentResult(404, "Item not found");
        }
        updateEntityOutPort.updateItemEntity(sku, item);
        return new NoContentResult();
    }

    @CacheInvalidate(cacheName = "items")
    @Override
    public NoContentResult deleteItem(@CacheKey String sku) {
        if (!itemRepository.existsBySku(sku)) {
            return new NoContentResult(404, "Item not found");
        }
        deleteEntityOutPort.deleteItemEntity(sku);
        return new NoContentResult();
    }

    @CacheInvalidate(cacheName = "orders")
    @Override
    public NoContentResult createOrder(Order order) {
        NoContentResult result = this.persistOrderPort.persistOrder(order);
        if (!result.hasError()) {
            System.out.println("Order has been successfully created" + order.getStatus());
        }
        return result;
    }

    @CacheInvalidate(cacheName = "orders")
    @Override
    public NoContentResult updateOrder(@CacheKey Long id, Order order) {
        Order existing = readOrderPort.readOrder(id);
        if (existing == null) {
            return new NoContentResult(404, "Order not found");
        }
        updateEntityOutPort.updateOrderEntity(id, order);
        return new NoContentResult();
    }

    @CacheInvalidate(cacheName = "orders")
    @Override
    public NoContentResult deleteOrder(@CacheKey Long orderId) {
        Order order = readOrderPort.readOrder(orderId);
        if (order == null) {
            return new NoContentResult(404, "Order not found");
        }
        if (order.getStatus() != OrderStatus.CREATED) {
            return new NoContentResult(409, "Cannot delete order in progress");
        }
        deleteEntityOutPort.deleteOrderEntity(orderId);
        return new NoContentResult();
    }

    @CacheInvalidate(cacheName = "employees")
    @Override
    public NoContentResult createEmployee(Employee employee) {
        return this.persistEmployeePort.persistEmployee(employee);
    }

    @CacheInvalidate(cacheName = "employees")
    @Override
    public NoContentResult updateEmployee(@CacheKey Long id, Employee employee) {
        updateEntityOutPort.updateEmployeeEntity(id, employee);
        return new NoContentResult();
    }

    @CacheInvalidate(cacheName = "employees")
    @Override
    public NoContentResult deleteEmployee(@CacheKey Long employeeId) {
        deleteEntityOutPort.deleteEmployeeEntity(employeeId);
        return new NoContentResult();
    }

    // Order operations that modify order state should invalidate cache
    @CacheInvalidate(cacheName = "orders")
    @Override
    public NoContentResult assignOrder(@CacheKey Long id, Long employeeId) {
        return this.assignOrderOutPort.updateOrder(id, employeeId);
    }

    @CacheInvalidate(cacheName = "orders")
    @Override
    public NoContentResult completeOrder(@CacheKey Long id, OrderStatus finalStatus) {
        Order order = readOrderPort.readOrder(id);
        if (order == null) {
            return new NoContentResult(404, "Order not found.");
        }
        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            return new NoContentResult(409, "Precondition failed: Order must be IN_PROGRESS. Current: " + order.getStatus());
        }
        if (finalStatus != OrderStatus.DONE && finalStatus != OrderStatus.FAILED) {
            return new NoContentResult(400, "Invalid status. Use DONE or FAILED.");
        }
        return this.completeOrderOutPort.completeOrder(id, finalStatus);
    }

    // OrderItem operations
    @CacheInvalidate(cacheName = "orderItems")
    @Override
    public NoContentResult createOrderItem(OrderItem orderItem) {
        Order order = this.readOrderPort.readOrder(orderItem.getOrderId());
        if (order == null) {
            NoContentResult error = new NoContentResult();
            error.setError(404, "Order not found");
            return error;
        }
        if (order.getStatus() != OrderStatus.CREATED) {
            NoContentResult error = new NoContentResult();
            error.setError(409, "Precondition Failed: Order is in state " + order.getStatus());
            return error;
        }
        return this.orderItemRepository.saveOrderItem(orderItem);
    }

    @CacheInvalidate(cacheName = "orderItems")
    @Override
    public NoContentResult pickOrderItem(@CacheKey Long orderItemId, int amount) {
        OrderItem orderItem = this.orderItemRepository.findById(orderItemId);
        if (orderItem == null) {
            return new NoContentResult(404, "Item not found");
        }
        if (amount > (orderItem.getQtyRequired() - orderItem.getQtyPicked())) {
            NoContentResult error = new NoContentResult();
            error.setError(400, "Amount is too big");
            return error;
        }
        return this.orderItemPickOutPort.pickOrderItem(orderItemId, amount);
    }

    @CacheInvalidate(cacheName = "orderItems")
    @Override
    public NoContentResult updateOrderItem(@CacheKey Long id, OrderItem orderItem) {
        OrderItem existing = orderItemRepository.findById(id);
        if (existing == null) {
            return new NoContentResult(404, "OrderItem not found");
        }
        updateEntityOutPort.updateOrderItemEntity(id, orderItem);
        return new NoContentResult();
    }

    @CacheInvalidate(cacheName = "orderItems")
    @Override
    public NoContentResult deleteOrderItem(@CacheKey Long orderItemId) {
        deleteEntityOutPort.deleteOrderItemEntity(orderItemId);
        return new NoContentResult();
    }

}

