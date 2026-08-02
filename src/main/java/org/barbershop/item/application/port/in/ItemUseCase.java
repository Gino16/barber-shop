package org.barbershop.item.application.port.in;

import java.util.Optional;
import org.barbershop.common.pagination.PagedResponse;
import org.barbershop.item.application.ItemCommand;
import org.barbershop.item.application.ItemFilterQuery;
import org.barbershop.item.domain.Item;

public interface ItemUseCase {

  PagedResponse<Item> list(ItemFilterQuery query);

  Optional<Item> findById(Long id);

  Item create(ItemCommand command);

  Optional<Item> update(Long id, ItemCommand command);
}
