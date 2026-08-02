package org.barbershop.item.application.port.out;

import java.util.List;
import java.util.Optional;
import org.barbershop.item.application.ItemFilterQuery;
import org.barbershop.item.domain.Item;

public interface ItemRepositoryPort {

  List<Item> findAll();

  Optional<Item> findById(Long id);

  Item save(Item item);

  List<Item> find(ItemFilterQuery query);

  long count(ItemFilterQuery query);
}
