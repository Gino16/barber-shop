package org.barbershop.item.application.port.out;

import org.barbershop.item.application.ItemFilterQuery;
import org.barbershop.item.domain.Item;
import java.util.List;
import java.util.Optional;

public interface ItemRepositoryPort {
    List<Item> findAll();
    Optional<Item> findById(Long id);
    Item save(Item item);
    List<Item> find(ItemFilterQuery query);
    long count(ItemFilterQuery query);
}
