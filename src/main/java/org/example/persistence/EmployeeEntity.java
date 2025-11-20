package org.example.persistence;

import jakarta.persistence.*;
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
    private String active;

    @Column
    private String shift;

    // One Employee can handle many Orders
    @ManyToOne
    private OrderEntity orders;

    public EmployeeEntity() {
    }
    public EmployeeEntity(String name, String active, String shift, OrderEntity orders) {
        this.Name = name;
        this.active = active;
        this.shift = shift;
        this.orders = orders;
    }
    public Long getEmployeeId() {return employeeId;}
    public void setEmployeeId(Long employeeId) {this.employeeId = employeeId;}
    public String getName() {return Name;}
    public void setName(String name) {this.Name = name;}
    public String getActive() {return active;}
    public void setActive(String active) {this.active = active;}

}
