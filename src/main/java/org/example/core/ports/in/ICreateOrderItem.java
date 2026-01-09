package org.example.core.ports.in;

import org.example.core.domain.OrderItem;
import org.example.core.results.NoContentResult;

public interface ICreateOrderItem {

    public NoContentResult createOrderItem(OrderItem orderItem);
}
