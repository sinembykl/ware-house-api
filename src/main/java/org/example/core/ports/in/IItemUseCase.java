package org.example.core.ports.in;


import org.example.core.domain.Item;

//inner port
public interface IItemUseCase {

    Item createItem(int sku, String item_name, String location);
}
