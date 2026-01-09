package org.example.core.ports.in;

import org.example.core.domain.Order;

import java.util.List;

public interface ILoadOrderUseCase {

    public List<Order> loadOrders(Long  orderId);
}
