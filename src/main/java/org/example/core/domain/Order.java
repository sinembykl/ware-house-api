package org.example.core.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    /*
    user input
   {
   id: 123456, anzahl: 5
   id: 129834, anzahl: 6
           ...
   }
     */
    private Long order_id;
    private String store; //filiale
    private int unit;
    private OrderStatus status;
    private int priority;
    private LocalDateTime CreatedAt;


    // One Order can have more than 1 OrderEntity
    List<OrderItem> orderItems = new ArrayList<>();

    public Order( String store, int unit) {
        this.store = store;
        this.unit = unit;
        this.status = OrderStatus.CREATED;
    }
    public Order() {}

    public long getOrder_id(){
        return order_id;
    }
    public String getStore(){
        return store;
    }
    public int getUnit(){
        return unit;
    }
    public OrderStatus getStatus(){return status;}

    public void setOrder_id(long order_id){
        this.order_id = order_id;
    }
    public void setStore(String store){
        this.store = store;
    }
    public void setUnit(int unit){
        this.unit = unit;
    }
    public void setStatus(OrderStatus status){this.status = status;}

    // CHANGE: Accept List<OrderItem> to match what the Mapper generates
    public void setOrderItems(List<OrderItem> items) {
        this.orderItems = items;
    }

    // Ensure the getter returns the same list for the Mapper/Jackson
    public List<OrderItem> getOrderItems() {
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        return orderItems;
    }



}
