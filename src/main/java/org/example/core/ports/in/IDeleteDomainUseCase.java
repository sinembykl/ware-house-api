package org.example.core.ports.in;
import org.example.core.results.NoContentResult;

public interface IDeleteDomainUseCase {
    NoContentResult deleteItem(String sku);
    NoContentResult deleteOrder(Long orderId);
    NoContentResult deleteEmployee(Long employeeId);
    NoContentResult deleteOrderItem(Long orderItemId);
}
