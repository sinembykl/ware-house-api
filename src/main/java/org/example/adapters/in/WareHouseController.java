package org.example.adapters.in;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
import org.example.core.domain.OrderItem;
import org.example.core.results.NoContentResult;

import java.util.List;
// NEW LINE REQUIRED: Import the DTO from the same package
//import org.example.adapters.ItemCreationRequest;

@Path("/warehouse")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WareHouseController {

    @Inject
    WarehouseFacade facade;


    // POST/items: Create a new Item
    @Path("/item")
    @POST
    public Response createItem(ItemCreationRequest request){
            NoContentResult result = facade.createItem(request);

            // if (result.isError())
        return Response.status(Response.Status.CREATED).build();
    }

    @Path("/items")
    @GET
    public Response findAllItems() {


        List<Item> result= (List<Item>) facade.findAllItems();
        return Response.ok(new GenericEntity<List<Item>>(result) {}).build();

    }

    @Path("/order")
    @POST
    public Response createOrder(OrderCreationRequest request){



        NoContentResult result = this.facade.createOrder(request);

        // Return 201 created
        return Response.status(Response.Status.CREATED)
                .entity(result)
                .build();

    }

    @Path("/order/{id}")
    @GET
    public Response findOrderById(@PathParam("id") Long id){

        List<Order> orders = facade.findAllOrders(id);

        if(orders.isEmpty()){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(new GenericEntity<List<Order>>(orders) {}).build();
    }

    @Path("order/{id}/items")
    @POST
    public Response addOrderItems(@PathParam("id") Long id, OrderItemCreationRequest request) {
        // Correctly captures the ID from the URL
        request.setOrderId(id);

        NoContentResult result = this.facade.createOrderItem(request);

        if (result.hasError()) {
            // Return the actual error code (409, 404, etc.) instead of hardcoded 400
            return Response.status(result.getErrorCode())
                    .entity(result) // Return the result so you can see the message in Postman
                    .build();
        }

        return Response.status(Response.Status.CREATED).entity(result).build();
    }



}
























