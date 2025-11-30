package org.example.core.ports.out;

import org.example.core.domain.Item;

import java.util.List;

public interface IReadItemsPort {

    public List<Item> readItems();
}
