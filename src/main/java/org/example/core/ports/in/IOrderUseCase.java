package org.example.core.ports.in;

import org.example.core.domain.Order;
import org.example.core.results.NoContentResult;

public interface IOrderUseCase {
    NoContentResult createOrder(Order order);
}
