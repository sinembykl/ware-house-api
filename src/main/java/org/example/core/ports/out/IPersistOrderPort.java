package org.example.core.ports.out;

import org.example.core.domain.Order;
import org.example.core.results.NoContentResult;

public interface IPersistOrderPort {
    NoContentResult persistOrder(Order order);
}
