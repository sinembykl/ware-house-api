package org.example.core.ports.out;

import org.example.core.results.NoContentResult;
import org.example.persistence.OrderStatus;

public interface ICompleteOrderOutPort {
    NoContentResult completeOrder(Long id, OrderStatus finalStatus);

}
