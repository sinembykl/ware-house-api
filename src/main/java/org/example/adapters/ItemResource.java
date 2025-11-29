package org.example.adapters;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.core.domain.Item;
import org.example.core.ports.in.IItemUseCase;
// NEW LINE REQUIRED: Import the DTO from the same package
//import org.example.adapters.ItemCreationRequest;

@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemResource {
    /*
    Translates HTTP/JSON requests into calls to the Inner Port
     */

    // Inject the Core's Inbound Port

    public static class ItemCreationRequest {
        public int sku;
        public String name;
        public String location;

        // Ensure you have a public no-arg constructor if using private fields/setters,
        // but public fields (as shown) often suffice for DTOs.
        public ItemCreationRequest() {}
    }

    @Inject
    IItemUseCase itemUseCase;

    // POST/items: Create a new Item
    @POST
    public Response createItem(ItemCreationRequest request){
            // Calling core logic via Inner Port
            Item item = itemUseCase.createItem(request.sku, request.name, request.location);
        return Response.created(null) // or Response.created(URI.create("/items/" + item.getItem_id()))
                .entity(item)
                .build();
    }

}
























