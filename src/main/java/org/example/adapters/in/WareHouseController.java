package org.example.adapters.in;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import io.quarkus.cache.CacheResult;
import io.quarkus.cache.CacheKey;
import org.example.core.domain.Employee;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.domain.OrderItem;
import org.example.core.results.NoContentResult;

import java.util.List;

@Path("/warehouse")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WareHouseController {

    @Inject
    WarehouseFacade facade;

    @Path("/item")
    @POST
    public Response createItem(ItemCreationRequest request){
        facade.createItem(request);
        return Response.status(Response.Status.CREATED).build();
    }

    @Path("/items")
    @GET
    public Response findAllItems() {
        List<Item> result = (List<Item>) facade.findAllItems();
        return Response.ok(new GenericEntity<List<Item>>(result) {}).build();
    }

    @Path("/order")
    @POST
    public Response createOrder(OrderCreationRequest request){
        NoContentResult result = this.facade.createOrder(request);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @Path("/order/{id}")
    @GET
    public Response findOrderById(@PathParam("id") @CacheKey Long id){
        Order order = facade.findAllOrder(id);
        if(order == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(order).build();
    }

    @Path("order/{id}/items")
    @POST
    public Response addOrderItems(@PathParam("id") Long id, OrderItemCreationRequest request) {
        NoContentResult result = this.facade.createOrderItem(id, request);
        if (result.hasError()) {
            return Response.status(result.getErrorCode()).entity(result).build();
        }
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @Path("orderItem/{id}/pick")
    @PUT
    public Response pickOrderItem(@PathParam("id") Long id, OrderItemPickRequest request){
        NoContentResult result = this.facade.pickOrderItem(id, request);
        if (result.hasError()) {
            return Response.status(result.getErrorCode()).entity(result).build();
        }
        return Response.status(Response.Status.ACCEPTED).entity(result).build();
    }

    @Path("employee")
    @POST
    public Response createEmployee(EmployeeCreationObject request) {
        NoContentResult result = this.facade.createEmployee(request);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @Path("/employee/{id}")
    @GET
    public Response findEmployeeById(@PathParam("id") @CacheKey Long id) {
        Employee employee = facade.findEmployeeById(id);
        if (employee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(employee).build();
    }

    @Path("order/{orderid}/assign/{employeeId}")
    @PUT
    public Response assignOrder(@PathParam("orderid") Long id, @PathParam("employeeId") Long employeeId){
        NoContentResult result = this.facade.assignOrder(id, employeeId);
        if (result.hasError()) {
            return Response.status(result.getErrorCode()).entity(result).build();
        }
        return Response.ok(result).build();
    }

    @Path("/order/{id}/complete")
    @PUT
    public Response completeOrder(@PathParam("id") Long id, CompletionRequest request) {
        NoContentResult result = facade.completeOrder(id, request);
        if (result.hasError()) {
            return Response.status(result.getErrorCode()).entity(result).build();
        }
        return Response.ok(result).build();
    }

    @Path("/item/{sku}")
    @GET
    public Response findItemBySku(@PathParam("sku") @CacheKey String sku) {
        Item item = facade.loadItem(sku);
        if (item == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(item).build();
    }

    @Path("/employee/{id}")
    @PUT
    public Response updateEmployee(@PathParam("id") Long id, EmployeeCreationObject request) {
        NoContentResult result = facade.updateEmployee(id, request);
        return result.hasError() ?
                Response.status(result.getErrorCode()).entity(result).build() :
                Response.ok().build();
    }

    @Path("/item/{sku}")
    @PUT
    public Response updateItem(@PathParam("sku") String sku, ItemCreationRequest request) {
        NoContentResult result = facade.updateItem(sku, request);
        if (result.hasError()) {
            return Response.status(result.getErrorCode()).entity(result).build();
        }
        return Response.ok().build();
    }

    @Path("/order/{id}")
    @PUT
    public Response updateOrder(@PathParam("id") Long id, OrderCreationRequest request) {
        NoContentResult result = facade.updateOrder(id, request);
        if (result.hasError()) {
            return Response.status(result.getErrorCode()).entity(result).build();
        }
        return Response.ok().build();
    }

    @Path("/employee/{id}")
    @DELETE
    public Response deleteEmployee(@PathParam("id") Long id) {
        NoContentResult result = facade.deleteEmployee(id);
        if (result.hasError()) {
            return Response.status(result.getErrorCode()).entity(result).build();
        }
        return Response.noContent().build();
    }
}
























