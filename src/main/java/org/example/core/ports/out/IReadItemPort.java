package org.example.core.ports.out;

import org.example.core.domain.Item;

import java.util.List;

public interface IReadItemPort {

    public List<Item> readItems();
    Item readItemBySku(String sku); // Added for direct DB lookup


}
