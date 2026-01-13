package org.example.ports.in;

import org.example.core.results.NoContentResult;
import org.example.core.domain.OrderStatus;

public interface ICompleteOrder {
    NoContentResult completeOrder(Long id, OrderStatus finalStatus);
}
