package org.example.core.ports.out;

import org.example.core.domain.OrderItem;
import org.example.core.results.NoContentResult;

public interface IOrderItemRepository {

    public NoContentResult saveOrderItem(OrderItem orderItem);

    OrderItem findById(Long id); // NEW: To fetch existing data for picking
}
