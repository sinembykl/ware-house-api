package org.example.core.ports.out;

import org.example.core.domain.Order;

import java.util.List;

public interface IReadOrderPort {

    public Order readOrder(Long orderId);
}
