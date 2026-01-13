package org.example.ports.out;

import org.example.core.results.NoContentResult;

public interface IOrderItemPickOutPort {
    public NoContentResult pickOrderItem(Long orderItemId, int amount);
}
