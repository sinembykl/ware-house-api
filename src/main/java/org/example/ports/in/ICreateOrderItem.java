package org.example.ports.in;

import org.example.core.domain.OrderItem;
import org.example.core.results.NoContentResult;

public interface ICreateOrderItem {

    public NoContentResult createOrderItem(OrderItem orderItem);

    OrderItem findById(Long id); // NEW: To fetch existing data for picking

}
