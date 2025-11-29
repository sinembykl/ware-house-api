package org.example.persistence;

import jakarta.persistence.*;
import org.example.core.domain.Order;

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
    private int priority;

    @Column
    private OrderStatus status;

    @Column
    private LocalDateTime CreatedAt;

    @Column
    private int unit;

    @ManyToOne
    OrderItemEntity orderItemEntity;

    //One order has many Order Items
    //List<OrderItemEntity> items;

    public OrderEntity() {

    }

    public OrderEntity(Order order) {
        //mapping
        this.store = order.getStore();
        this.priority = 1;
        this.status = OrderStatus.IN_PROGRESS;
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


}
