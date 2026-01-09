package org.example.persistence;

import jakarta.persistence.*;
import org.example.core.domain.Order;
import org.example.core.domain.OrderItem;

import java.time.LocalDateTime;
import java.util.Collection;
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


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderItemEntity> orderItem;



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
    // CHANGE: Accept List<OrderItem> to match what the Mapper generates

    public void setOrderItemEntities(List<OrderItemEntity> orderItem) {
        this.orderItem = orderItem;
    }
    public List<OrderItemEntity> getOrderItemEntities() {return orderItem;}

}
