package org.example.persistence;


import jakarta.persistence.*;
import org.example.core.domain.Item;
import org.example.core.domain.OrderItem;

@Entity
public class OrderItemEntity {
    /*
    OrderItem — represents a single product line in an order.
    Attributes: id, qtyRequired, qtyPicked, location, order, item
    Rule: qtyPicked ≤ qtyRequired
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int qtyRequired;

    @Column
    private int qtyPicked;

    @Column
    private String location;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name="item_id")
    private ItemEntity item;


    public OrderItemEntity() {}

    public OrderItemEntity(OrderItem orderItem) {
        this.qtyRequired = orderItem.getQtyRequired();
        this.qtyPicked = orderItem.getQtyPicked();
        this.location = orderItem.getLocation();

    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public int getQtyRequired() {return qtyRequired;}
    public void setQtyRequired(int qtyRequired) {this.qtyRequired = qtyRequired;}
    public int getQtyPicked() {return qtyPicked;}
    public void setQtyPicked(int qtyPicked) {this.qtyPicked = qtyPicked;}
    public String getLocation() {return location;}
    public void setLocation(String location) {this.location = location;}
    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }
    public ItemEntity getItem() { return item; }
    public void setItem(ItemEntity item) { this.item = item; }


}
