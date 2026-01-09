package org.example.adapters.in;

public class OrderItemCreationRequest {
    public Long orderId;
    public String sku;
    public int qtyReq;

    public OrderItemCreationRequest() {

    }

    public void setOrderId(Long orderId){
        this.orderId = orderId;
    }
}
