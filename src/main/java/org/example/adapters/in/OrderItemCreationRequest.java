package org.example.adapters.in;


public class OrderItemCreationRequest {
    public String sku;
    public int qtyReq;

    public OrderItemCreationRequest() {}

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQtyReq() {
        return qtyReq;
    }

    public void setQtyReq(int qtyReq) {
        this.qtyReq = qtyReq;
    }
}
