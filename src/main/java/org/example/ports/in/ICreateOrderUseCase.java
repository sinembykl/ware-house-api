package org.example.ports.in;

import org.example.core.domain.Order;
import org.example.core.results.NoContentResult;

public interface ICreateOrderUseCase {
    NoContentResult createOrder(Order order);
}
