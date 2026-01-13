package org.example.ports.in;

import org.example.core.domain.Order;

public interface ILoadOrder {

    public Order  loadOrder(Long  orderId);
}
