package org.example.core.ports.out;

import org.example.core.domain.Item;
import org.example.core.results.NoContentResult;
import org.example.persistence.OrderStatus;

//output port for saving the data
// it is an interface that dictates the functions the core needs from external systems
public interface IItemRepository {

    NoContentResult createItem(Item item);

    boolean existsBySku(String sku);
}
