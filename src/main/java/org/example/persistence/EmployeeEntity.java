package org.example.persistence;

import jakarta.persistence.*;
import org.example.adapters.in.OrderCreationRequest;
import org.example.core.domain.Employee;
import org.example.core.domain.Order;

import java.util.List;

@Entity
public class EmployeeEntity {
    /*
    Employee — represents a warehouse worker.
    Attributes: id, name, active, shift
    Rule: Only active employees can receive assignments

    One Employee can handle many Orders, but an Order belongs to at most one
    Employee.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @Column
    private String Name;

    @Column
    private boolean active;

    @Column
    private String shift;

    // One Employee can handle many Orders
    @OneToMany(mappedBy = "employee",cascade = CascadeType.ALL)
    private List<OrderEntity> orders;

    public EmployeeEntity() {
    }
    public EmployeeEntity(Employee employee) {
        this.Name = employee.getName();
        this.active = employee.isActive();
        this.shift = employee.getShift();

    }
    public Long getEmployeeId() {return employeeId;}
    public void setEmployeeId(Long employeeId) {this.employeeId = employeeId;}
    public String getName() {return Name;}
    public void setName(String name) {this.Name = name;}
    public boolean isActive() {return active;}
    public void activate() {this.active = true;}
    public void deactivate() {this.active = false;}
    public List<OrderEntity> getOrders() { return orders; }
    public void setOrders(List<OrderEntity> orders) { this.orders = orders; }

}
