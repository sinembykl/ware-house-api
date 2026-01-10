package org.example.core.domain;

public class OrderItem {

    // {order_id:12345, item_id : 54321, unit: 5}

    long id;
    long orderId;
    String item_sku;
    int qtyRequired; // 5 unit
    int qtyPicked; // how much we have in the warehouse
    String location;
    int order_status;// 0-2
    Item item;

    public OrderItem(long orderId, String item_sku, int qtyReq) {
        this.orderId = orderId;
        this.item_sku = item_sku;
        this.qtyRequired = qtyReq;
    }
    public OrderItem(long orderId){
        this.orderId = orderId;
    }
    public OrderItem(){
    }

    // Rule: qtyPicked ≤ qtyRequired
    public boolean isValid() {
        return qtyPicked <= qtyRequired;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getOrderId() {
        return orderId;
    }
    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
    public String getItem_sku() {
        return item_sku;
    }
    public void setItem_sku(String item_sku) {
        this.item_sku = item_sku;
    }
    public int getQtyRequired() {
        return qtyRequired;
    }
    public void setQtyRequired(int qtyRequired) {
        this.qtyRequired = qtyRequired;
    }
    public int getQtyPicked() {
        return qtyPicked;
    }
    public void setQtyPicked(int qtyPicked) {
        this.qtyPicked = qtyPicked;
    }
    public int getOrder_status() {
        return order_status;
    }
    public void setOrder_status(int order_status) {
        this.order_status = order_status;
    }
    public Item getItem() {
        return item;
    }
    public void setItem(Item item) {
        this.item = item;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }



}
