package org.example.core.ports;

import org.example.persistence.OrderEntity;
import org.example.persistence.OrderStatus;

public interface IOrderRepository {

    void createOrder(String store, int priority, OrderStatus status);
}
