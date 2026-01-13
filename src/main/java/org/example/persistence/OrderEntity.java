package org.example.persistence;

import jakarta.persistence.*;
import org.example.core.domain.Order;
import org.example.core.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long order_id;

    @Column
    private String store;

    @Column
    private int priority; // 1, 2, 3

    @Column
    private OrderStatus status;

    @Column
    private LocalDateTime CreatedAt;

    @Column
    private int unit;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    List<OrderItemEntity> orderItem;


    // Many Orders can belong to one Employee
    @ManyToOne
    @JoinColumn(name = "employee_id") // This creates the foreign key in the Order table
    private EmployeeEntity employee;



    public OrderEntity() {

    }

    public OrderEntity(Order order) {
        //mapping
        this.store = order.getStore();
        this.priority = 1;
        this.status = order.getStatus();
        CreatedAt = LocalDateTime.now();
        this.unit = order.getUnit();
    }

    public Long getOrder_id() {return order_id;}
    public void setOrder_id(Long order_id) {this.order_id = order_id;}
    public String getStore() {return store;}
    public void setStore(String store) {this.store = store;}
    public int getPriority() {return priority;}
    public void setPriority(int priority) {this.priority = priority;}
    public OrderStatus getStatus() {return status;}
    public void setStatus(OrderStatus status) {this.status = status;}
    public LocalDateTime getCreatedAt() {return CreatedAt;}
    public void setCreatedAt(LocalDateTime CreatedAt) {this.CreatedAt = CreatedAt;}
    public int getUnit() {return unit;}
    public void setUnit(int unit) {this.unit = unit;}
    public List<OrderItemEntity> getOrderItemEntities() {return orderItem;}
    public void setOrderItemEntites(List<OrderItemEntity> orderItemEntities) {this.orderItem = orderItemEntities;}

    public EmployeeEntity getEmployee() { return employee; }
    public void setEmployee(EmployeeEntity employee) { this.employee = employee; }

}
