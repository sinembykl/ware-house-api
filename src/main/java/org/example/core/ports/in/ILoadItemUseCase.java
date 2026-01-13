package org.example.core.ports.in;

import org.example.core.domain.Item;

import java.util.List;

public interface ILoadItemUseCase {

    public List<Item> loadAllItems();

    Item loadItem(String sku);
}
