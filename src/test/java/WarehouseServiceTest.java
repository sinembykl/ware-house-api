import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.example.core.domain.Employee;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.domain.OrderItem;
import org.example.core.logic.WarehouseService;
import org.example.core.results.NoContentResult;
import org.example.core.domain.OrderStatus;
import org.example.ports.out.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@QuarkusTest
public class WarehouseServiceTest {

    @Inject
    WarehouseService warehouseService;

    @InjectMock
    IReadOrderPort readOrderPort;
    @InjectMock
    IItemRepository itemRepository;
    @InjectMock
    IOrderItemRepository orderItemRepository;
    @InjectMock
    IAssignOrderOutPort assignOrderOutPort;
    @InjectMock
    IPersistOrderPort persistOrderPort;
    @InjectMock
    IReadItemPort readItemsPort;
    @InjectMock
    ICompleteOrderOutPort completeOrderOutPort;
    @InjectMock
    IOrderItemPickOutPort orderItemPickOutPort;
    @InjectMock
    IPersistEmployeePort persistEmployeePort;
    @InjectMock
    IDeleteEntityOutPort deleteEntityOutPort; // Added for delete coverage
    @InjectMock
    IUpdateEntityOutPort updateEntityOutPort; // Added for update coverage

    // --- ITEM TESTS ---

    @Test
    public void testCreateItem_Success() {
        Item item = new Item("A13", "New Item", "Hubland");
        Mockito.when(itemRepository.existsBySku("A13")).thenReturn(false);
        Mockito.when(itemRepository.createItem(item)).thenReturn(new NoContentResult());

        NoContentResult result = warehouseService.createItem(item);
        Assertions.assertFalse(result.hasError());
    }

    @Test
    public void testCreateItem_FailIfSkuExists() {
        Mockito.when(itemRepository.existsBySku("A12")).thenReturn(true);
        Item duplicateItem = new Item();
        duplicateItem.setSku("A12");

        NoContentResult result = warehouseService.createItem(duplicateItem);
        Assertions.assertTrue(result.hasError());
        Assertions.assertEquals(400, result.getErrorCode());
    }

    @Test
    public void testUpdateItem_NotFound() { // Missing Case 1
        Mockito.when(itemRepository.existsBySku("MISSING")).thenReturn(false);
        NoContentResult result = warehouseService.updateItem("MISSING", new Item());
        Assertions.assertEquals(404, result.getErrorCode());
    }

    @Test
    public void testDeleteItem_NotFound() { // Missing Case 2
        Mockito.when(itemRepository.existsBySku("MISSING")).thenReturn(false);
        NoContentResult result = warehouseService.deleteItem("MISSING");
        Assertions.assertEquals(404, result.getErrorCode());
    }

    @Test
    public void testLoadAllItems() {
        Mockito.when(readItemsPort.readItems()).thenReturn(Arrays.asList(new Item(), new Item()));
        List<Item> items = warehouseService.loadAllItems();
        Assertions.assertEquals(2, items.size());
    }

    // --- ORDER TESTS ---

    @Test
    public void testCreateOrder_Success() {
        Order order = new Order();
        Mockito.when(persistOrderPort.persistOrder(order)).thenReturn(new NoContentResult());

        NoContentResult result = warehouseService.createOrder(order);
        Assertions.assertFalse(result.hasError());
    }

    @Test
    public void testUpdateOrder_NotFound() { // Missing Case 3
        Mockito.when(readOrderPort.readOrder(99L)).thenReturn(null);
        NoContentResult result = warehouseService.updateOrder(99L, new Order());
        Assertions.assertEquals(404, result.getErrorCode());
    }

    @Test
    public void testDeleteOrder_NotFound() { // Missing Case 4
        Mockito.when(readOrderPort.readOrder(99L)).thenReturn(null);
        NoContentResult result = warehouseService.deleteOrder(99L);
        Assertions.assertEquals(404, result.getErrorCode());
    }

    @Test
    public void testDeleteOrder_FailIfInProgress() { // Missing Case 5
        Order order = new Order();
        order.setStatus(OrderStatus.IN_PROGRESS);
        Mockito.when(readOrderPort.readOrder(1L)).thenReturn(order);

        NoContentResult result = warehouseService.deleteOrder(1L);
        Assertions.assertEquals(409, result.getErrorCode());
    }

    @Test
    public void testLoadOrder() {
        Order mockOrder = new Order();
        Mockito.when(readOrderPort.readOrder(1L)).thenReturn(mockOrder);

        Order result = warehouseService.loadOrder(1L);
        Assertions.assertNotNull(result);
    }

    // --- ORDER ITEM TESTS ---

    @Test
    public void testCreateOrderItem_Success() {
        Order mockOrder = new Order();
        mockOrder.setStatus(OrderStatus.CREATED);
        Mockito.when(readOrderPort.readOrder(1L)).thenReturn(mockOrder);

        OrderItem newItem = new OrderItem();
        newItem.setOrderId(1L);
        Mockito.when(orderItemRepository.saveOrderItem(newItem)).thenReturn(new NoContentResult());

        NoContentResult result = warehouseService.createOrderItem(newItem);
        Assertions.assertFalse(result.hasError());
    }

    @Test
    public void testCreateOrderItem_OrderNotFound() { // Missing Case 6
        Mockito.when(readOrderPort.readOrder(99L)).thenReturn(null);
        OrderItem newItem = new OrderItem();
        newItem.setOrderId(99L);

        NoContentResult result = warehouseService.createOrderItem(newItem);
        Assertions.assertEquals(404, result.getErrorCode());
    }

    @Test
    public void testUpdateOrderItem_NotFound() { // Missing Case 7
        Mockito.when(orderItemRepository.findById(99L)).thenReturn(null);
        NoContentResult result = warehouseService.updateOrderItem(99L, new OrderItem());
        Assertions.assertEquals(404, result.getErrorCode());
    }

    // --- PICKING TESTS ---

    @Test
    public void testPickOrderItem_NotFound() { // Missing Case 8
        Mockito.when(orderItemRepository.findById(99L)).thenReturn(null);
        NoContentResult result = warehouseService.pickOrderItem(99L, 5);
        Assertions.assertEquals(404, result.getErrorCode());
    }

    // --- COMPLETION TESTS ---

    @Test
    public void testCompleteOrder_InvalidTargetStatus() { // Missing Case 9
        Order order = new Order();
        order.setStatus(OrderStatus.IN_PROGRESS);
        Mockito.when(readOrderPort.readOrder(1L)).thenReturn(order);

        // Trying to set it back to CREATED is forbidden
        NoContentResult result = warehouseService.completeOrder(1L, OrderStatus.CREATED);
        Assertions.assertEquals(400, result.getErrorCode());
    }

    // --- EXISTING TESTS RETAINED ---

    @Test
    public void testCreateOrderItem_FailIfOrderNotCreated() {
        Order mockOrder = new Order();
        mockOrder.setStatus(OrderStatus.IN_PROGRESS);
        Mockito.when(readOrderPort.readOrder(1L)).thenReturn(mockOrder);

        OrderItem newItem = new OrderItem();
        newItem.setOrderId(1L);

        NoContentResult result = warehouseService.createOrderItem(newItem);
        Assertions.assertEquals(409, result.getErrorCode());
    }

    @Test
    public void testPickOrderItem_Success() {
        OrderItem mockItem = new OrderItem();
        mockItem.setQtyRequired(10);
        mockItem.setQtyPicked(5);
        Mockito.when(orderItemRepository.findById(100L)).thenReturn(mockItem);
        Mockito.when(orderItemPickOutPort.pickOrderItem(100L, 2)).thenReturn(new NoContentResult());

        NoContentResult result = warehouseService.pickOrderItem(100L, 2);
        Assertions.assertFalse(result.hasError());
    }

    @Test
    public void testPickOrderItem_FailIfAmountTooHigh() {
        OrderItem mockItem = new OrderItem();
        mockItem.setQtyRequired(10);
        mockItem.setQtyPicked(5);
        Mockito.when(orderItemRepository.findById(100L)).thenReturn(mockItem);

        NoContentResult result = warehouseService.pickOrderItem(100L, 6);
        Assertions.assertEquals(400, result.getErrorCode());
    }

    @Test
    public void testCreateEmployee() {
        Employee emp = new Employee();
        Mockito.when(persistEmployeePort.persistEmployee(emp)).thenReturn(new NoContentResult());
        NoContentResult result = warehouseService.createEmployee(emp);
        Assertions.assertFalse(result.hasError());
    }

    @Test
    public void testAssignOrder_Success() {
        Mockito.when(assignOrderOutPort.updateOrder(1L, 1L)).thenReturn(new NoContentResult());
        NoContentResult result = warehouseService.assignOrder(1L, 1L);
        Assertions.assertFalse(result.hasError());
    }

    @Test
    public void testCompleteOrder_Success() {
        Order order = new Order();
        order.setStatus(OrderStatus.IN_PROGRESS);
        Mockito.when(readOrderPort.readOrder(1L)).thenReturn(order);
        Mockito.when(completeOrderOutPort.completeOrder(1L, OrderStatus.DONE)).thenReturn(new NoContentResult());

        NoContentResult result = warehouseService.completeOrder(1L, OrderStatus.DONE);
        Assertions.assertFalse(result.hasError());
    }

    @Test
    public void testCompleteOrder_FailWrongState() {
        Order order = new Order();
        order.setStatus(OrderStatus.CREATED);
        Mockito.when(readOrderPort.readOrder(1L)).thenReturn(order);

        NoContentResult result = warehouseService.completeOrder(1L, OrderStatus.DONE);
        Assertions.assertEquals(409, result.getErrorCode());
    }
}