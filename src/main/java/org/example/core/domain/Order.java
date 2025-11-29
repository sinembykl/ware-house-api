package org.example.core.domain;

public class Order {
    /*
    user input
   {
   id: 123456, anzahl: 5
   id: 129834, anzahl: 6
           ...
   }
     */
    private long order_id;
    private String store; //filiale
    private int unit;
    //String status; //finished, in process so on
    //String prio; // low, high, middle
    //Date created; // the date of creation
    //long employee_id;

    Order(long order_id, String store, int unit) {
        this.order_id = order_id;
        this.store = store;
        this.unit = unit;
    }
    Order() {}

    public long getOrder_id(){
        return order_id;
    }
    public String getStore(){
        return store;
    }
    public int getUnit(){
        return unit;
    }

    public void setOrder_id(long order_id){
        this.order_id = order_id;
    }
    public void setStore(String store){
        this.store = store;
    }
    public void setUnit(int unit){
        this.unit = unit;
    }



}
