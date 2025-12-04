package org.example.persistence;

import jakarta.persistence.*;
import org.example.core.domain.Item;

@Entity
public class ItemEntity {
    /*
    Item — represents a product stored in the warehouse.
    Attributes: id, sku, name, unit, defaultLocation
    Invariant: SKU must be unique
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long item_id;

    @Column
    private String sku;

    @Column(nullable = false)
    private String item_name;

    @Column
    private String location;

    public ItemEntity() {

    }

    public ItemEntity(Item item) {
        //this.item_id = item.getItem_id(); // it will automatically generated
        this.sku = item.getSku();
        this.item_name = item.getName();
        this.location = item.getLocation();
    }

    public Long getItem_id() {return this.item_id;}
    public void setItem_id(Long item_id) {this.item_id = item_id;}
    public String getSku() {return this.sku;}
    public void setSku(String sku) {this.sku = sku;}
    public String getItem_name() {return this.item_name;}
    public void setItem_name(String item_name) {this.item_name = item_name;}
    public String getLocation() {return this.location;}
    public void setLocation(String location) {this.location = location;}


}
