package org.example.core.ports.out;

import org.example.core.domain.Item;
import org.example.persistence.OrderStatus;

//output port for saving the data
// it is an interface that dictates the functions the core needs from external systems
public interface IItemRepository {

    public Item createItem(Item item);

    interface IOrderRepository {

        void createOrder(String store, int priority, OrderStatus status);
    }
}
