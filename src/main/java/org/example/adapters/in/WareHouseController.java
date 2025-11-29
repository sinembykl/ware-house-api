package org.example.adapters.in;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.adapters.out.WarehouseServiceAdapter;
import org.example.core.domain.Item;
import org.example.core.domain.Order;
// NEW LINE REQUIRED: Import the DTO from the same package
//import org.example.adapters.ItemCreationRequest;

@Path("/warehouse")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WareHouseController {
    /*
    Translates HTTP/JSON requests into calls to the Inner Port
     */


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


    @Path("/order")
    @POST
    public Response createOrder(OrderCreationRequest request){
        // add DTO as a seperate class and delete inner classes !!

        Order order = new Order(1,request.store, request.unit);


        this.adapter.persistOrder(order);

        // Return 201 created
        return Response.status(Response.Status.CREATED).build();

    }


}
























