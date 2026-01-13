import jakarta.inject.Inject;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.example.adapters.out.WarehouseServiceAdapter;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.domain.OrderItem;
import org.example.persistence.EmployeeEntity;
import org.example.persistence.ItemEntity;
import org.example.persistence.OrderEntity;
import org.example.persistence.OrderStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class WarehousePersistenceTest {


    @Inject
    WarehouseServiceAdapter adapter; // Test the adapter directly
    @Inject
    EntityManager em;

    @Test
    @Transactional
    public void testOrderItemLinking() {
        // 1. Setup Item (Ensure all NOT NULL fields are set)
        ItemEntity ie = new ItemEntity();
        ie.setSku("SKU-99");
        ie.setItem_name("Mandatory Name"); // Fixes the PropertyValueException
        ie.setLocation("Warehouse-A");
        em.persist(ie);

        // 2. Setup Order
        OrderEntity oe = new OrderEntity();
        oe.setStore("Lidl");
        oe.setStatus(OrderStatus.CREATED);
        em.persist(oe);

        // 3. Create Link
        OrderItem domainItem = new OrderItem();
        domainItem.setOrderId(oe.getOrder_id());
        domainItem.setItem_sku("SKU-99");
        domainItem.setQtyRequired(10);
        adapter.saveOrderItem(domainItem);

        // 4. Sync
        em.flush();
        em.clear();

        // 5. Assertions using your readOrder method
        Order savedOrder = adapter.readOrder(oe.getOrder_id());
        savedOrder.getOrderItems().forEach(item -> {
            System.out.printf("DEBUG - OrderItemID: %d | SKU: %s | Qty: %d/%d%n",
                    item.getId(),
                    item.getItem_sku(),
                    item.getQtyPicked(),
                    item.getQtyRequired());
        });

        Assertions.assertNotNull(savedOrder);
        Assertions.assertNotNull(savedOrder.getOrderItems());

    }
    @Test
    @Transactional
    public void testAssignEmployeeToOrder() {
        // 1. SETUP: Create a real Employee in the DB
        EmployeeEntity employee = new EmployeeEntity();
        employee.setName("Sinem");
        employee.activate();
        employee.setShift("late");
        em.persist(employee); // Direct DB write

        // 2. SETUP: Create a real Order in the DB
        OrderEntity order = new OrderEntity();
        order.setStore("Lidl");
        order.setStatus(OrderStatus.CREATED);
        order.setUnit(100);
        em.persist(order); // Direct DB write

        // 3. ACTION: Use the adapter to assign the order
        // This triggers your logic to change status to IN_PROGRESS
        adapter.updateOrder(order.getOrder_id(), employee.getEmployeeId());

        // 4. SYNC: Flush and Clear to force a fresh read from MariaDB
        em.flush();
        em.clear();

        // 5. VERIFY: Re-fetch the order and check the employee link
        Order savedOrder = adapter.readOrder(order.getOrder_id());

        Assertions.assertNotNull(savedOrder);
        Assertions.assertEquals(OrderStatus.IN_PROGRESS, savedOrder.getStatus()); // Verify logic
        // If you have employeeId in your Domain Order, check it here
    }


    }

