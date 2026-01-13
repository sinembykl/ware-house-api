package org.example.ports.in;

import org.example.core.results.NoContentResult;
import org.example.persistence.OrderStatus;

public interface ICompleteOrder {
    NoContentResult completeOrder(Long id, OrderStatus finalStatus);
}
