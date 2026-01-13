package org.example.core.logic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.core.domain.Employee;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.domain.OrderItem;
import org.example.core.results.NoContentResult;
import org.example.persistence.OrderStatus;
import org.example.ports.in.*;
import org.example.ports.out.*;

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
    public Order loadOrder(Long orderId){

        return this.readOrderPort.readOrder(orderId);
    }
    @Override
    public boolean existsBySku(String sku){
        return this.itemRepository.existsBySku(sku);
    }

    @Override
    public NoContentResult createOrderItem(OrderItem orderItem) {
        // 1. Fetch the existing order
        Order order = this.readOrderPort.readOrder(orderItem.getOrderId());

        if (order == null) {
            NoContentResult error = new NoContentResult();
            error.setError(404, "Order not found");
            return error;
        }

        Order existingOrder = order;

        // 2. APPLY PRECONDITION: Order must be in CREATED state [cite: 86]
        if (existingOrder.getStatus() != OrderStatus.CREATED) {
            NoContentResult error = new NoContentResult();
            // UC-03 Failure Requirement: Invalid state -> 409 Conflict [cite: 88]
            error.setError(409, "Precondition Failed: Order is in state " + existingOrder.getStatus());
            return error;
        }

        // 3. If check passes, proceed to save [cite: 87, 89]
        return this.orderItemRepository.saveOrderItem(orderItem);
    }

    @Override
    public NoContentResult createEmployee(Employee employee){

        return this.persistEmployeePort.persistEmployee(employee);
    }
    @Override
    @Transactional
    public NoContentResult assignOrder(Long id, Long employeeId) {
        return this.assignOrderOutPort.updateOrder(id, employeeId);
    }

    // Inside WarehouseService.java
    @Override
    @Transactional
    public NoContentResult completeOrder(Long id, OrderStatus finalStatus) {
        // 1. Load the order using the CORRECT name: readOrder
        // Note: Assuming readOrder returns a single Order or Optional<Order>
        Order order = readOrderPort.readOrder(id);

        if (order == null) {
            return new NoContentResult(404, "Order not found.");
        }

        // 2. Precondition: Order must be IN_PROGRESS
        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            return new NoContentResult(409, "Precondition failed: Order must be IN_PROGRESS. Current: " + order.getStatus());
        }

        // 3. Validation: finalStatus must be DONE or FAILED
        if (finalStatus != OrderStatus.DONE && finalStatus != OrderStatus.FAILED) {
            return new NoContentResult(400, "Invalid status. Use DONE or FAILED.");
        }

        // 4. Update status and save via specialized Port
        // We don't need persistOrder here because the OutPort handles the update directly
        return this.completeOrderOutPort.completeOrder(id, finalStatus);
    }
    @Override
    public NoContentResult pickOrderItem(Long orderItemId, int amount) {
        OrderItem orderItem = this.orderItemRepository.findById(orderItemId);
        if (orderItem == null) {
            return new NoContentResult(404, "Item not found");
        }

        // Now qtyRequired and qtyPicked are the ACTUAL values from the DB
        if (amount > (orderItem.getQtyRequired() - orderItem.getQtyPicked())) {
            NoContentResult error = new NoContentResult();
            error.setError(400, "Amount is too big");
            return error;
        }

        return this.orderItemPickOutPort.pickOrderItem(orderItemId, amount);
    }

    @Override
    public OrderItem findById(Long id){
        return this.orderItemRepository.findById(id);
    }

    @Override
    @Transactional
    public NoContentResult deleteOrder(Long orderId) {
        Order order = readOrderPort.readOrder(orderId);
        if (order == null) {
            return new NoContentResult(404, "Order not found");
        }
        // Business Rule: Only delete orders in CREATED state
        if (order.getStatus() != OrderStatus.CREATED) {
            return new NoContentResult(409, "Cannot delete order in progress");
        }
        deleteEntityOutPort.deleteOrderEntity(orderId);
        return new NoContentResult();
    }

    @Override
    @Transactional
    public NoContentResult deleteItem(String sku) {
        if (!itemRepository.existsBySku(sku)) {
            return new NoContentResult(404, "Item not found");
        }
        deleteEntityOutPort.deleteItemEntity(sku);
        return new NoContentResult();
    }

    @Override
    @Transactional
    public NoContentResult deleteEmployee(Long employeeId) {
        deleteEntityOutPort.deleteEmployeeEntity(employeeId);
        return new NoContentResult();
    }

    @Override
    @Transactional
    public NoContentResult deleteOrderItem(Long orderItemId) {
        deleteEntityOutPort.deleteOrderItemEntity(orderItemId);
        return new NoContentResult();
    }

    @Override
    public Item loadItem(String sku) {
        // 1. Logic: Check if SKU is valid/exists
        if (sku == null || sku.isEmpty()) {
            return null;
        }

        // 2. Call the Outer Port (IReadItemPort) to fetch the data
        return this.readItemsPort.readItemBySku(sku);
    }

    @Override
    @Transactional
    public NoContentResult updateItem(String sku, Item item) {
        if (!itemRepository.existsBySku(sku)) {
            return new NoContentResult(404, "Item not found");
        }
        updateEntityOutPort.updateItemEntity(sku, item);
        return new NoContentResult();
    }

    @Override
    @Transactional
    public NoContentResult updateOrder(Long id, Order order) {
        Order existing = readOrderPort.readOrder(id);
        if (existing == null) {
            return new NoContentResult(404, "Order not found");
        }
        updateEntityOutPort.updateOrderEntity(id, order);
        return new NoContentResult();
    }
    @Override
    @Transactional
    public NoContentResult updateEmployee(Long id, Employee employee) {
        // Validation logic can be added here (e.g., check if name is empty)
        updateEntityOutPort.updateEmployeeEntity(id, employee);
        return new NoContentResult();
    }

    @Override
    @Transactional
    public NoContentResult updateOrderItem(Long id, OrderItem orderItem) {
        OrderItem existing = orderItemRepository.findById(id);
        if (existing == null) {
            return new NoContentResult(404, "OrderItem not found");
        }

        // You might want to check if the parent order is still in CREATED state
        updateEntityOutPort.updateOrderItemEntity(id, orderItem);
        return new NoContentResult();
    }
    @Override
    @Transactional
    public Employee getEmployee(Long employeeId) {
        // Logic: Call the outer port to fetch the employee
        return this.persistEmployeePort.readEmployee(employeeId);
    }



}

