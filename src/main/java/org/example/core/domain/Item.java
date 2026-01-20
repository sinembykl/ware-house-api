package org.example.core.domain;

public class Item {
    private String sku;
    private String name;
    private String location;
    private Long item_id;

    // MANDATORY: Default constructor
    public Item() {}

    // Constructor used in your test
    public Item(String sku, String name, String location) {
        this.sku = sku;
        this.name = name;
        this.location = location;
    }

    // MANDATORY: Getters for all fields used in test assertions
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public Long getItem_id() { return item_id; }

    // Setters (Recommended for completeness)
    public void setSku(String sku) { this.sku = sku; }
    public void setName(String name) { this.name = name; }
    public void setLocation(String location) { this.location = location; }
    public void setItem_id(Long item_id) { this.item_id = item_id; }
}