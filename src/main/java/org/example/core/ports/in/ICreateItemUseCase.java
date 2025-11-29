package org.example.core.ports.in;


import org.example.core.domain.Item;
import org.example.core.results.NoContentResult;

//inner port
public interface ICreateItemUseCase {

    NoContentResult createItem(Item item);
}
