package org.example.persistence;

import jakarta.persistence.*;

@Entity
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int qtyRequired;
    private int qtyPicked;
    private String location;

    // Links this record to the specific Order
    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    // Links this record to the specific Item
    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    public OrderItemEntity() {}

    // Getters and Setters
    public Long getId() { return id; }
    public int getQtyRequired() { return qtyRequired; }
    public void setQtyRequired(int qtyRequired) { this.qtyRequired = qtyRequired; }
    public int getQtyPicked() { return qtyPicked; }
    public void setQtyPicked(int qtyPicked) { this.qtyPicked = qtyPicked; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }

    public ItemEntity getItem() { return item; }
    public void setItem(ItemEntity item) { this.item = item; }
}