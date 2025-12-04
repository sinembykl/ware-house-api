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

    /*
    //bi tane daha adapter controller ile domain arasina koy ve orada übersetzung yapilsin
     */

    @Inject
    WarehouseFacade adapter;


    // POST/items: Create a new Item
    @Path("/items")
    @POST
    public Response createItem(ItemCreationRequest request){
            // Calling core logic via Inner Port
            this.adapter.createItem(request);
        return Response.status(Response.Status.CREATED).build();
    }

    @Path("/items")
    @GET
    public Response findAllItems() {


        List<Item> result= (List<Item>) adapter.findAllItems();
        return Response.ok(new GenericEntity<List<Item>>(result) {}).build();

    }


    //itempotent?
    @Path("/order")
    @POST
    public Response createOrder(OrderCreationRequest request){



        this.adapter.createOrder(request);

        // Return 201 created
        return Response.status(Response.Status.CREATED).build();

    }




}
























