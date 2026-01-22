package org.example.adapters.in;

import org.example.core.domain.Order;
import org.example.core.domain.OrderItem;
import org.example.core.domain.OrderStatus;
import org.example.persistence.OrderItemEntity;

import java.util.List;

public class OrderResponse {
    public long id;
    public String store;
    public int unit;
    public OrderStatus status;
    public List<OrderItem> orderItems;    // This field name will be the JSON key
    public Links _links = new Links();

    public static OrderResponse from(Order o) {
        OrderResponse r = new OrderResponse();
        r.id = o.getOrder_id();
        r.store = o.getStore();
        r.unit = o.getUnit();
        r.status = o.getStatus();
        r.orderItems = o.getOrderItems();
        return r;
    }
}