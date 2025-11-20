package org.example.core.ports;

import org.example.core.domain.Employee;
import org.example.core.domain.Order;

//port
public interface WareHouseRepository {

    void createOrder(Order order);
    void createEmployee(Employee employee);
    void updateOrder(Order order);
    void updateEmployee(Employee employee);
    void deleteOrder(Order order);
    Order findOrderById(long id);
    Employee findEmployeeById(long id);

}
