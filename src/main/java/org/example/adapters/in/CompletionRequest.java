package org.example.adapters.in;


import org.example.core.domain.OrderStatus;
public class CompletionRequest {
    private OrderStatus status;

    public CompletionRequest() {}

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}