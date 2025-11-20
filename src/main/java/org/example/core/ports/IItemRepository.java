package org.example.core.ports;

import org.example.core.domain.Item;

//output port for saving the data
// it is an interface that dictates the functions the core needs from external systems
public interface IItemRepository {

    public Item createItem(Item item);
}
