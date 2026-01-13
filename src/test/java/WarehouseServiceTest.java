
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

    // --- ITEM TESTS ---

    @Test
    public void testCreateItem_Success() {
        Item item = new Item("A13", "New Item","Hubland");
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
    public void testCreateOrderItem_FailIfOrderNotCreated() {
        Order mockOrder = new Order();
        mockOrder.setStatus(OrderStatus.IN_PROGRESS);
        Mockito.when(readOrderPort.readOrder(1L)).thenReturn(mockOrder);

        OrderItem newItem = new OrderItem();
        newItem.setOrderId(1L);

        NoContentResult result = warehouseService.createOrderItem(newItem);
        Assertions.assertEquals(409, result.getErrorCode());
    }

    // --- PICKING TESTS ---

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

    // --- EMPLOYEE & ASSIGNMENT TESTS ---

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

    // --- COMPLETION TESTS ---

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