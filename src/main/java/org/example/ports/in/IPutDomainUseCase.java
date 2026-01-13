package org.example.ports.in;
import org.example.core.domain.*;
import org.example.core.results.NoContentResult;

public interface IPutDomainUseCase {
    NoContentResult updateItem(String sku, Item item);
    NoContentResult updateOrder(Long id, Order order);
    NoContentResult updateEmployee(Long id, Employee employee);
    NoContentResult updateOrderItem(Long id, OrderItem orderItem);
}
