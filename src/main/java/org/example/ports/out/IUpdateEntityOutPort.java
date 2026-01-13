package org.example.ports.out;
import org.example.core.domain.*;

public interface IUpdateEntityOutPort {
    void updateItemEntity(String sku, Item item);
    void updateOrderEntity(Long id, Order order);
    void updateEmployeeEntity(Long id, Employee employee);
    void updateOrderItemEntity(Long id, OrderItem orderItem);
}