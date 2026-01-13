package org.example.adapters.in;


public class OrderCreationRequest {
    public String store;
    public int unit;

    public OrderCreationRequest() {}

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }
}