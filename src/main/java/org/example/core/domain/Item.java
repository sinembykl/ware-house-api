package org.example.core.domain;

public class Item {
    private int sku;
    Long item_id;
    String name;
    String location;
    //String description;

    public Item(int sku,String name, String location) {
        this.sku = sku;
        this.location = location;
        this.name = name;
    }
    public long getItem_id() {
        return item_id;
    }
    public void setItem_id(long item_id) {
        this.item_id = item_id;
    }
    public int getSku() {
        return sku;
    }
    public void setSku(int sku) {
        this.sku = sku;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }


    
}
