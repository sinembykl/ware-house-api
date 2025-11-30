package org.example.core.ports.in;

import org.example.core.domain.Item;

import java.util.List;

public interface ILoadAllItemUseCase {

    public List<Item> loadAllItems();
}
