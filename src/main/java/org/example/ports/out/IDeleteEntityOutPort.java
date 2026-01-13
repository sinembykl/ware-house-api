package org.example.ports.out;


public interface IDeleteEntityOutPort {
    void deleteItemEntity(String sku);
    void deleteOrderEntity(Long orderId);
    void deleteEmployeeEntity(Long employeeId);
    void deleteOrderItemEntity(Long orderItemId);
}
