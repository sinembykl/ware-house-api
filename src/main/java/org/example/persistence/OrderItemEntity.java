package org.example.persistence;


import jakarta.persistence.*;
import org.example.core.domain.Item;

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



    //ItemEntity item;

    public OrderItemEntity() {}
    public OrderItemEntity( int qtyRequired, int qtyPicked, String location) {
        this.qtyRequired = qtyRequired;
        this.qtyPicked = qtyPicked;
        this.location = location;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public int getQtyRequired() {return qtyRequired;}
    public void setQtyRequired(int qtyRequired) {this.qtyRequired = qtyRequired;}
    public int getQtyPicked() {return qtyPicked;}
    public void setQtyPicked(int qtyPicked) {this.qtyPicked = qtyPicked;}
    public String getLocation() {return location;}
    public void setLocation(String location) {this.location = location;}



}
