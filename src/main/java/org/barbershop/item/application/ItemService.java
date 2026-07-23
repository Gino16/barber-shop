package org.barbershop.item.application;

import org.barbershop.item.application.port.in.ItemUseCase;
import org.barbershop.item.application.port.out.ItemRepositoryPort;
import org.barbershop.item.domain.Item;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@ApplicationScoped
public class ItemService implements ItemUseCase {

  private final ItemRepositoryPort repository;

  @Inject
  public ItemService(ItemRepositoryPort repository) {
    this.repository = repository;
  }

  @Override
  public PagedResponse<Item> list(ItemFilterQuery query) {
    ItemFilterQuery validatedQuery = query.withDefaults();
    var items = repository.find(validatedQuery);
    long total = repository.count(validatedQuery);
    return new PagedResponse<>(items, validatedQuery.page(), validatedQuery.pageSize(), total);
  }

  @Override
  public Optional<Item> findById(Long id) {
    return repository.findById(id);
  }

  @Override
  public Item create(ItemCommand command) {
    return repository.save(new Item(null, command.name(), command.description(), command.category(),
        command.active() == null || command.active(), OffsetDateTime.now(ZoneOffset.UTC)));
  }

  @Override
  public Optional<Item> update(Long id, ItemCommand command) {
    return repository.findById(id)
        .map(existing -> repository.save(new Item(existing.id(), command.name(),
            command.description(), command.category(), command.active() == null || command.active(),
            existing.createdAt())));
  }
}
