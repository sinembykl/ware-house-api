package org.example.core.domain;

public class OrderItem {

    // {order_id:12345, item_id : 54321, unit: 5}

    long id;
    long orderId;
    long productId;
    int qtyReq; // 5 unit
    int qtyPick; // how much we have in the warehouse
    //int location;
    int order_status;// 0-2

    
    // feedback for order_status
    void order_check(){
        if (qtyPick >= qtyReq){
            order_status = 2;
        }  else if (qtyPick == 0) {
            order_status = 0;
        }  else{
            order_status = 1;
        }
    }



}
