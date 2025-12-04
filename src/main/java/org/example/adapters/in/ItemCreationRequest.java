package org.example.adapters.in;

// DTO for now
public class ItemCreationRequest {
    public String sku;
    public String name;
    public String location;

    // Ensure you have a public no-arg constructor if using private fields/setters,
    // but public fields (as shown) often suffice for DTOs.
    public ItemCreationRequest() {}
}
