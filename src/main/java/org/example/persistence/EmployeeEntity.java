package org.example.persistence;

import jakarta.persistence.*;
import org.example.core.domain.Employee;

import java.util.List;
@Entity
public class EmployeeEntity {
    /*
    Employee – represents a warehouse worker.
    Attributes: id, name, active, shift
    Rule: Only active employees can receive assignments

    One Employee can handle many Orders, but an Order belongs to at most one
    Employee.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Changed from employeeId to id

    @Column
    private String name;

    @Column
    private boolean active;

    @Column
    private String shift;

    // One Employee can handle many Orders
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<OrderEntity> orders;

    public EmployeeEntity() {
    }

    public EmployeeEntity(Employee employee) {
        this.name = employee.getName();
        this.active = employee.isActive();
        this.shift = employee.getShift();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public List<OrderEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderEntity> orders) {
        this.orders = orders;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getShift() {
        return shift;
    }
}
