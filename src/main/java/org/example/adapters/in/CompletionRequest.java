package org.example.adapters.in;


import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.example.core.domain.OrderStatus;
public class CompletionRequest {
    @Schema(defaultValue = "DONE", example = "DONE")
    private OrderStatus status;

    public CompletionRequest() {
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}