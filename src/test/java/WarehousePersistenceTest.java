import jakarta.inject.Inject;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.example.adapters.out.WarehouseServiceAdapter;
import org.example.core.domain.*;
import org.example.core.results.NoContentResult;
import org.example.persistence.*;
import org.example.core.domain.OrderStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@QuarkusTest
public class WarehousePersistenceTest {

    @Inject
    WarehouseServiceAdapter adapter;
    @Inject
    EntityManager em;

    // --- ITEM PERSISTENCE TESTS ---

    @Test
    @Transactional
    public void testCreateItem() {
        Item item = new Item("SKU-1", "Test Item", "A-1");
        NoContentResult result = adapter.createItem(item);

        Assertions.assertFalse(result.hasError());
        Assertions.assertNotNull(em.find(ItemEntity.class, item.getItem_id()));
    }

    @Test
    @Transactional
    public void testExistsBySku() {
        ItemEntity ie = new ItemEntity();
        ie.setSku("EXISTING");
        ie.setItem_name("Name");
        ie.setLocation("Loc");
        em.persist(ie);

        Assertions.assertTrue(adapter.existsBySku("EXISTING"));
        Assertions.assertFalse(adapter.existsBySku("MISSING"));
    }

    @Test
    @Transactional
    public void testReadItems_Pagination() {
        for (int i = 0; i < 3; i++) {
            ItemEntity ie = new ItemEntity();
            ie.setSku("S" + i);
            ie.setItem_name("N");
            ie.setLocation("Loc-X");
            em.persist(ie);
        }

        List<Item> page1 = adapter.readItems("Loc-X", 2, 0);
        Assertions.assertEquals(2, page1.size());

        List<Item> page2 = adapter.readItems("Loc-X", 2, 2);
        Assertions.assertEquals(1, page2.size());
    }

    @Test
    @Transactional
    public void testUpdateItem_Flush() {
        ItemEntity ie = new ItemEntity();
        ie.setSku("U1");
        ie.setItem_name("Old");
        ie.setLocation("OldLoc");
        em.persist(ie);
        em.flush();

        Item update = new Item("U1", "New Name", "NewLoc");
        adapter.updateItemEntity("U1", update);

        em.clear();
        ItemEntity updated = em.createQuery("select i from ItemEntity i where i.sku = 'U1'", ItemEntity.class).getSingleResult();
        Assertions.assertEquals("New Name", updated.getItem_name());
    }

    @Test
    @Transactional
    public void testDeleteItemEntity_BySku() {
        ItemEntity ie = new ItemEntity();
        ie.setSku("DEL-SKU");
        ie.setItem_name("N");
        ie.setLocation("L");
        em.persist(ie);

        adapter.deleteItemEntity("DEL-SKU");
        em.flush();
        em.clear();

        Assertions.assertTrue(em.createQuery("select i from ItemEntity i where i.sku = 'DEL-SKU'").getResultList().isEmpty());
    }

    // --- ORDER PERSISTENCE TESTS ---

    @Test
    @Transactional
    public void testPersistOrder() {
        Order order = new Order("Store A", 50);
        NoContentResult result = adapter.persistOrder(order);

        Assertions.assertNotNull(result.getId());
        Assertions.assertNotNull(em.find(OrderEntity.class, result.getId()));
    }

    @Test
    @Transactional
    public void testCompleteOrder() {
        OrderEntity oe = new OrderEntity();
        oe.setStatus(OrderStatus.IN_PROGRESS);
        em.persist(oe);

        adapter.completeOrder(oe.getOrder_id(), OrderStatus.DONE);
        em.flush();
        em.clear();

        OrderEntity result = em.find(OrderEntity.class, oe.getOrder_id());
        Assertions.assertEquals(OrderStatus.DONE, result.getStatus());
    }

    @Test
    @Transactional
    public void testDeleteOrderEntity() {
        OrderEntity oe = new OrderEntity();
        em.persist(oe);

        adapter.deleteOrderEntity(oe.getOrder_id());
        Assertions.assertNull(em.find(OrderEntity.class, oe.getOrder_id()));
    }

    // --- ORDER ITEM & LINKING TESTS ---

    @Test
    @Transactional
    public void testOrderItemLinking() {
        // 1. Setup Item (Ensure all fields are set)
        ItemEntity ie = new ItemEntity();
        ie.setSku("SKU-99");
        ie.setItem_name("Mandatory Name");
        ie.setLocation("Warehouse-A");
        em.persist(ie);

        // 2. Setup Order
        OrderEntity oe = new OrderEntity();
        oe.setStore("Lidl");
        oe.setStatus(OrderStatus.CREATED);
        em.persist(oe);

        // --- CRITICAL FIX ---
        // Force Hibernate to push the Item and Order to DB so the Adapter's JPQL can find them
        em.flush();

        // 3. Create Link using Adapter logic
        OrderItem domainItem = new OrderItem();
        domainItem.setOrderId(oe.getOrder_id()); // ID is now available after flush
        domainItem.setItem_sku("SKU-99");
        domainItem.setQtyRequired(10);

        NoContentResult result = adapter.saveOrderItem(domainItem);

        // 4. Verification with detailed error message
        Assertions.assertFalse(result.hasError(),
                "Adapter failed with message: " + result.getErrorMessage());

        // 5. Final Sync and verification
        em.flush();
        em.clear();

        Order savedOrder = adapter.readOrder(oe.getOrder_id());
        Assertions.assertNotNull(savedOrder);
        Assertions.assertFalse(savedOrder.getOrderItems().isEmpty(), "Order should have items linked");
        Assertions.assertEquals("SKU-99", savedOrder.getOrderItems().get(0).getItem_sku());
    }

    @Test
    @Transactional
    public void testSaveOrderItem_SkuNotFound() {
        OrderEntity oe = new OrderEntity();
        em.persist(oe);

        OrderItem domainItem = new OrderItem();
        domainItem.setOrderId(oe.getOrder_id());
        domainItem.setItem_sku("MISSING-SKU");

        NoContentResult result = adapter.saveOrderItem(domainItem);
        Assertions.assertEquals(404, result.getErrorCode());
    }

    @Test
    @Transactional
    public void testPickOrderItem_Accumulation() {
        OrderItemEntity oie = new OrderItemEntity();
        oie.setQtyPicked(2);
        em.persist(oie);

        adapter.pickOrderItem(oie.getId(), 3);
        em.flush();
        em.clear();

        OrderItemEntity updated = em.find(OrderItemEntity.class, oie.getId());
        Assertions.assertEquals(5, updated.getQtyPicked());
    }

    // --- EMPLOYEE PERSISTENCE TESTS ---

    @Test
    @Transactional
    public void testPersistEmployee() {
        Employee emp = new Employee("Sinem", true, "late");
        NoContentResult result = adapter.persistEmployee(emp);

        Assertions.assertNotNull(result.getId());
        EmployeeEntity entity = em.find(EmployeeEntity.class, result.getId());
        Assertions.assertEquals("Sinem", entity.getName());
    }

    @Test
    @Transactional
    public void testReadEmployee_Mapping() {
        EmployeeEntity ee = new EmployeeEntity();
        ee.setName("John");
        ee.setShift("morning");
        ee.activate();
        em.persist(ee);
        em.flush();

        Employee result = adapter.readEmployee(ee.getId());
        Assertions.assertEquals("John", result.getName());
        Assertions.assertTrue(result.isActive());
    }

    @Test
    @Transactional
    public void testUpdateEmployee_ToggleActive() {
        EmployeeEntity ee = new EmployeeEntity();
        ee.activate();
        em.persist(ee);

        Employee update = new Employee();
        update.deactivate();

        adapter.updateEmployeeEntity(ee.getId(), update);
        em.flush();
        em.clear();

        EmployeeEntity result = em.find(EmployeeEntity.class, ee.getId());
        Assertions.assertFalse(result.isActive());
    }

    @Test
    @Transactional
    public void testAssignEmployeeToOrder() {
        EmployeeEntity ee = new EmployeeEntity();
        em.persist(ee);

        OrderEntity oe = new OrderEntity();
        oe.setStatus(OrderStatus.CREATED);
        em.persist(oe);

        adapter.updateOrder(oe.getOrder_id(), ee.getId());
        em.flush();
        em.clear();

        OrderEntity updated = em.find(OrderEntity.class, oe.getOrder_id());
        Assertions.assertEquals(OrderStatus.IN_PROGRESS, updated.getStatus());
        Assertions.assertEquals(ee.getId(), updated.getEmployee().getId());
    }

    @Test
    @Transactional
    public void testDeleteEmployeeEntity() {
        EmployeeEntity ee = new EmployeeEntity();
        em.persist(ee);

        adapter.deleteEmployeeEntity(ee.getId());
        Assertions.assertNull(em.find(EmployeeEntity.class, ee.getId()));
    }
}