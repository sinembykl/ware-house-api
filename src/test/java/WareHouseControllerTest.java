
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.example.adapters.in.*;
import org.example.core.domain.Employee;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.results.NoContentResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class WareHouseControllerTest {

    @InjectMock
    WarehouseFacade facade;

    // --- ITEM ENDPOINTS (5 Cases) ---

    @Test
    public void testCreateItem_Success() {
        Mockito.when(facade.createItem(any())).thenReturn(new NoContentResult());

        given()
                .contentType(ContentType.JSON)
                .body("{\"sku\":\"A1\", \"name\":\"Item1\", \"location\":\"Loc1\"}")
                .when().post("/warehouse/item")
                .then().statusCode(201);
    }

    @Test
    public void testCreateItem_Fail_Duplicate() {
        NoContentResult error = new NoContentResult(400, "SKU exists");
        Mockito.when(facade.createItem(any())).thenReturn(error);

        given()
                .contentType(ContentType.JSON)
                .body("{\"sku\":\"A1\"}")
                .when().post("/warehouse/item")
                .then().statusCode(400)
                .body("errorMessage", equalTo("SKU exists"));
    }

    @Test
    public void testFindAllItems_Pagination() {
        Mockito.when(facade.findItems(any(), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        given()
                .queryParam("limit", 10)
                .when().get("/warehouse/items")
                .then().statusCode(200);
    }

    @Test
    public void testFindItemBySku_Success() {
        Item mockItem = new Item("A1", "Test Item", "Storage-01");

        // Use Mockito.anyString() to ensure the mock triggers regardless of string nuances
        Mockito.when(facade.loadItem(Mockito.anyString())).thenReturn(mockItem);

        given()
                .pathParam("sku", "A1")
                .when().get("/warehouse/item/{sku}")
                .then()
                .statusCode(200)
                .body("sku", equalTo("A1"));
    }
    @Test
    public void testUpdateItem_Success() {
        Mockito.when(facade.updateItem(anyString(), any())).thenReturn(new NoContentResult());

        given()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"New\"}")
                .when().put("/warehouse/item/A1")
                .then().statusCode(200);
    }

    // --- ORDER ENDPOINTS (6 Cases) ---

    @Test
    public void testCreateOrder_HATEOAS() {
        NoContentResult res = new NoContentResult();
        res.setId(100L);
        Mockito.when(facade.createOrder(any())).thenReturn(res);

        given()
                .contentType(ContentType.JSON)
                .body("{}")
                .when().post("/warehouse/order")
                .then().statusCode(201)
                .body("_links.self", containsString("/warehouse/order/100"));
    }

    @Test
    public void testFindOrderById_Success() {
        Order order = new Order();
        order.setOrder_id(5L);
        Mockito.when(facade.findAllOrder(5L)).thenReturn(order);

        given()
                .when().get("/warehouse/order/5")
                .then().statusCode(200).body("_links.self", notNullValue());
    }

    @Test
    public void testFindOrderById_NotFound() {
        Mockito.when(facade.findAllOrder(99L)).thenReturn(null);
        given().when().get("/warehouse/order/99").then().statusCode(404);
    }

    @Test
    public void testUpdateOrder_Success() {
        Mockito.when(facade.updateOrder(anyLong(), any())).thenReturn(new NoContentResult());
        given().contentType(ContentType.JSON).body("{}").when().put("/warehouse/order/1").then().statusCode(200);
    }

    @Test
    public void testCompleteOrder_Success() {
        Mockito.when(facade.completeOrder(anyLong(), any())).thenReturn(new NoContentResult());
        given().contentType(ContentType.JSON).body("{\"status\":\"DONE\"}").when().put("/warehouse/order/1/complete").then().statusCode(200);
    }

    @Test
    public void testCompleteOrder_Fail_Precondition() {
        NoContentResult err = new NoContentResult(409, "Must be In-Progress");
        Mockito.when(facade.completeOrder(anyLong(), any())).thenReturn(err);
        given().contentType(ContentType.JSON).body("{\"status\":\"DONE\"}").when().put("/warehouse/order/1/complete").then().statusCode(409);
    }

    // --- EMPLOYEE ENDPOINTS (5 Cases) ---

    @Test
    public void testCreateEmployee_Success() {
        Mockito.when(facade.createEmployee(any())).thenReturn(new NoContentResult());
        given().contentType(ContentType.JSON).body("{\"name\":\"E1\"}").when().post("/warehouse/employee").then().statusCode(201);
    }

    @Test
    public void testFindEmployeeById_Success() {
        Employee emp = new Employee();
        emp.setId(1L);
        Mockito.when(facade.findEmployeeById(1L)).thenReturn(emp);
        given().when().get("/warehouse/employee/1").then().statusCode(200);
    }

    @Test
    public void testUpdateEmployee_Success() {
        Mockito.when(facade.updateEmployee(anyLong(), any())).thenReturn(new NoContentResult());
        given().contentType(ContentType.JSON).body("{}").when().put("/warehouse/employee/1").then().statusCode(200);
    }

    @Test
    public void testDeleteEmployee_Success() {
        Mockito.when(facade.deleteEmployee(1L)).thenReturn(new NoContentResult());
        given().when().delete("/warehouse/employee/1").then().statusCode(204);
    }

    @Test
    public void testDeleteEmployee_Fail() {
        Mockito.when(facade.deleteEmployee(9L)).thenReturn(new NoContentResult(404, "Not found"));
        given().when().delete("/warehouse/employee/9").then().statusCode(404);
    }

    // --- WORKFLOW & OPERATIONS (7 Cases) ---

    @Test
    public void testAddOrderItems_Success() {
        Mockito.when(facade.createOrderItem(anyLong(), any())).thenReturn(new NoContentResult());
        given().contentType(ContentType.JSON).body("{}").when().post("/warehouse/order/1/items").then().statusCode(201);
    }

    @Test
    public void testAddOrderItems_OrderNotFound() {
        Mockito.when(facade.createOrderItem(anyLong(), any())).thenReturn(new NoContentResult(404, "Order not found"));
        given().contentType(ContentType.JSON).body("{}").when().post("/warehouse/order/99/items").then().statusCode(404);
    }

    @Test
    public void testPickOrderItem_Accepted() {
        Mockito.when(facade.pickOrderItem(anyLong(), any())).thenReturn(new NoContentResult());
        given().contentType(ContentType.JSON).body("{\"amount\":5}").when().put("/warehouse/orderItem/1/pick").then().statusCode(202);
    }

    @Test
    public void testPickOrderItem_TooMuch() {
        Mockito.when(facade.pickOrderItem(anyLong(), any())).thenReturn(new NoContentResult(400, "Too big"));
        given().contentType(ContentType.JSON).body("{\"amount\":100}").when().put("/warehouse/orderItem/1/pick").then().statusCode(400);
    }

    @Test
    public void testAssignOrder_Success() {
        Mockito.when(facade.assignOrder(anyLong(), anyLong())).thenReturn(new NoContentResult());
        given().when().put("/warehouse/order/1/assign/2").then().statusCode(200);
    }

    @Test
    public void testAssignOrder_EmployeeNotFound() {
        Mockito.when(facade.assignOrder(anyLong(), anyLong())).thenReturn(new NoContentResult(404, "Missing"));
        given().when().put("/warehouse/order/1/assign/9").then().statusCode(404);
    }

    @Test
    public void testHATEOASLinkConsistency() {
        Order order = new Order();
        order.setOrder_id(77L);
        Mockito.when(facade.findAllOrder(77L)).thenReturn(order);

        given()
                .when().get("/warehouse/order/77")
                .then().body("_links.addItem", containsString("/warehouse/order/77/items"));
    }

    // Helper methods for Mockito matchers
    private String anyString() { return Mockito.anyString(); }
    private Long anyLong() { return Mockito.anyLong(); }
    private Integer anyInt() { return Mockito.anyInt(); }
    private <T> T any() { return Mockito.any(); }
}