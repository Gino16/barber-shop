package org.barbershop.item.application;

import org.barbershop.audit.application.AuditLogger;
import org.barbershop.audit.domain.AuditAction;
import org.barbershop.item.application.port.in.ItemUseCase;
import org.barbershop.item.application.port.out.ItemRepositoryPort;
import org.barbershop.item.domain.Item;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class ItemService implements ItemUseCase {

  private final ItemRepositoryPort repository;
  private final AuditLogger auditLogger;

  @Inject
  public ItemService(ItemRepositoryPort repository, AuditLogger auditLogger) {
    this.repository = repository;
    this.auditLogger = auditLogger;
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
    Item created = repository.save(new Item(null, command.name(), command.description(), command.category(),
        command.active() == null || command.active(), OffsetDateTime.now(ZoneOffset.UTC)));
    auditLogger.record("ITEM", created.id(), AuditAction.CREATE, null, values(created));
    return created;
  }

  @Override
  public Optional<Item> update(Long id, ItemCommand command) {
    return repository.findById(id)
        .map(existing -> {
          Item updated = repository.save(new Item(existing.id(), command.name(),
              command.description(), command.category(), command.active() == null || command.active(),
              existing.createdAt()));
          auditLogger.record("ITEM", updated.id(), AuditAction.UPDATE, values(existing), values(updated));
          return updated;
        });
  }

  private Map<String, Object> values(Item item) {
    Map<String, Object> values = new LinkedHashMap<>();
    values.put("id", item.id());
    values.put("name", item.name());
    values.put("description", item.description());
    values.put("category", item.category());
    values.put("active", item.active());
    values.put("createdAt", item.createdAt());
    return values;
  }
}
