package org.example.adapters.in;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.adapters.out.WarehouseServiceAdapter;
import org.example.core.domain.Item;
import org.example.core.domain.Order;

import java.util.List;
// NEW LINE REQUIRED: Import the DTO from the same package
//import org.example.adapters.ItemCreationRequest;

@Path("/warehouse")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WareHouseController {
    /*
    Translates HTTP/JSON requests into calls to the Inner Port
     */

    // ??is it right to inject adapter directly or should i reach out inner port only??


    @Inject
    WarehouseServiceAdapter adapter;

    // DTO for now
    public static class ItemCreationRequest {
        public int sku;
        public String name;
        public String location;

        // Ensure you have a public no-arg constructor if using private fields/setters,
        // but public fields (as shown) often suffice for DTOs.
        public ItemCreationRequest() {}
    }

    // DTO to update later
    public static class OrderCreationRequest {
        public String store;
        public int unit;

        public OrderCreationRequest() {}

    }


    // POST/items: Create a new Item
    @Path("/items")
    @POST
    public Response createItem(ItemCreationRequest request){
            // Calling core logic via Inner Port
            Item item = new Item(request.sku, request.name, request.location);
            this.adapter.createItem(item);
        return Response.status(Response.Status.CREATED).build();
    }

    @Path("/items")
    @GET
    public Response findAllItems() {


        List<Item> result= (List<Item>) adapter.readItems();
        return Response.ok(new GenericEntity<List<Item>>(result) {}).build();

    }


    @Path("/order")
    @POST
    public Response createOrder(OrderCreationRequest request){
        // add DTO as a seperate class and delete inner classes !!

        Order order = new Order(request.store, request.unit);


        this.adapter.persistOrder(order);

        // Return 201 created
        return Response.status(Response.Status.CREATED).build();

    }




}
























