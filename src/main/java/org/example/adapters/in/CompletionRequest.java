package org.example.adapters.in;


import org.example.persistence.OrderStatus;

public class CompletionRequest {
    // This field maps to the "status" key in your JSON
    private OrderStatus status;

    // Default constructor for Jackson/Quarkus
    public CompletionRequest() {}

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

}