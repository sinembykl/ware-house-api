import org.example.adapters.out.WarehouseMapper;
import org.example.core.domain.*;
import org.example.core.domain.OrderStatus;
import org.example.persistence.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class WarehouseMapperTest {

    @Test
    public void testItemToDomain() {
        ItemEntity entity = new ItemEntity();
        entity.setItem_id(1L);
        entity.setSku("SKU-123");
        entity.setItem_name("Test Item");
        entity.setLocation("A1-B2");

        Item domain = WarehouseMapper.toDomain(entity);

        Assertions.assertNotNull(domain);
        Assertions.assertEquals(1L, domain.getItem_id());
        Assertions.assertEquals("SKU-123", domain.getSku());
        Assertions.assertEquals("Test Item", domain.getName());
        Assertions.assertEquals("A1-B2", domain.getLocation());
    }

    @Test
    public void testEmployeeToDomain() {
        EmployeeEntity entity = new EmployeeEntity();
        entity.setId(5L);
        entity.setName("Sinem");
        entity.setShift("late");
        entity.activate(); // Sets active = true

        Employee domain = WarehouseMapper.toDomain(entity);

        Assertions.assertNotNull(domain);
        Assertions.assertEquals(5L, domain.getId());
        Assertions.assertEquals("Sinem", domain.getName());
        Assertions.assertTrue(domain.isActive());
        Assertions.assertEquals("late", domain.getShift());
    }

    @Test
    public void testOrderToDomain_WithItems() {
        // 1. Setup Order Entity
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrder_id(10L);
        orderEntity.setStore("Lidl");
        orderEntity.setUnit(100);
        orderEntity.setStatus(OrderStatus.CREATED);

        // 2. Setup Linked Order Item Entity
        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setId(1L);
        itemEntity.setQtyRequired(10);

        // FIX: You must create the ItemEntity object first!
        ItemEntity ie = new ItemEntity();
        ie.setSku("SKU-99");
        ie.setItem_id(99L); // Ensure this is set to avoid the NPE

        // Link the Item to the OrderItem before calling methods that use it
        itemEntity.setItem(ie);

        // Now you can safely add it to the order
        orderEntity.getOrderItemEntities().add(itemEntity);

        // 3. Map
        Order domain = WarehouseMapper.toDomain(orderEntity);

        // 4. Assert
        Assertions.assertEquals(10L, domain.getOrder_id());
        Assertions.assertEquals(1, domain.getOrderItems().size());
        Assertions.assertEquals("SKU-99", domain.getOrderItems().get(0).getItem_sku());
    }
    @Test
    public void testOrderItemToDomain_DeepMapping() {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(1L);
        entity.setQtyRequired(5);
        entity.setQtyPicked(2);
        entity.setLocation("Warehouse-A");

        // Mock the relationship to ItemEntity
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setSku("DEEP-SKU");
        itemEntity.setItem_name("Deep Product");
        entity.setItem(itemEntity);

        OrderItem domain = WarehouseMapper.toDomain(entity);

        Assertions.assertNotNull(domain);
        Assertions.assertEquals("DEEP-SKU", domain.getItem_sku());
        Assertions.assertNotNull(domain.getItem()); // Verifies nested item mapping
        Assertions.assertEquals("Deep Product", domain.getItem().getName());
    }

    @Test
    public void testItemToEntity() {
        Item domain = new Item("SKU-XP", "Entity Test", "Loc-Z");

        ItemEntity entity = WarehouseMapper.toEntity(domain);

        Assertions.assertNotNull(entity);
        Assertions.assertEquals("SKU-XP", entity.getSku());
    }

    @Test
    public void testNullHandling() {
        Assertions.assertNull(WarehouseMapper.toDomain((ItemEntity) null));
        Assertions.assertNull(WarehouseMapper.toDomain((OrderEntity) null));
        Assertions.assertNull(WarehouseMapper.toDomain((EmployeeEntity) null));
    }
}