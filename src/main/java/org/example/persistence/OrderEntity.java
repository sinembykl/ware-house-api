package org.example.persistence;

import jakarta.persistence.*;
import org.example.core.domain.Order;
import org.example.core.domain.OrderStatus;
import java.util.ArrayList; // Necessary import
import java.util.List;

@Entity
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long order_id;

    private String store;
    private int unit;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeEntity employee;

    // CRITICAL FIX: Initialize the list to avoid NullPointerException
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItemEntity> orderItemEntities = new ArrayList<>();

    public OrderEntity() {}

    public OrderEntity(Order order) {
        this.store = order.getStore();
        this.unit = order.getUnit();
        this.status = order.getStatus() != null ? order.getStatus() : OrderStatus.CREATED;
    }

    // Getters and Setters
    public Long getOrder_id() { return order_id; }
    public String getStore() { return store; }
    public void setStore(String store) { this.store = store; }
    public int getUnit() { return unit; }
    public void setUnit(int unit) { this.unit = unit; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public EmployeeEntity getEmployee() { return employee; }
    public void setEmployee(EmployeeEntity employee) { this.employee = employee; }

    // This getter is what was returning null
    public List<OrderItemEntity> getOrderItemEntities() { return orderItemEntities; }
    public void setOrderItemEntities(List<OrderItemEntity> orderItemEntities) {
        this.orderItemEntities = orderItemEntities;
    }
}
