package org.example.ports.in;

import org.example.adapters.in.OrderItemPickRequest;
import org.example.core.results.NoContentResult;

public interface IOrderItemPickUseCase {
    public NoContentResult pickOrderItem(Long orderItemId, int amount);
}
