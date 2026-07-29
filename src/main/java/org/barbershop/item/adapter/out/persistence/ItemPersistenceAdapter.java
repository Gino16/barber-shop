package org.barbershop.item.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.barbershop.item.application.ItemFilterQuery;
import org.barbershop.item.application.port.out.ItemRepositoryPort;
import org.barbershop.item.domain.Item;

@ApplicationScoped
public class ItemPersistenceAdapter implements ItemRepositoryPort {

  @Inject
  ItemPanacheRepository panache;

  @Override
  public List<Item> findAll() {
    return panache.listAll().stream().map(ItemJpaEntity::toDomain).toList();
  }

  @Override
  public Optional<Item> findById(Long id) {
    return panache.find("id", id).firstResultOptional().map(ItemJpaEntity::toDomain);
  }

  @Override
  public List<Item> find(ItemFilterQuery query) {
    return buildQuery(query)
        .range(query.offset(), query.offset() + query.pageSize() - 1)
        .stream()
        .map(ItemJpaEntity::toDomain)
        .toList();
  }

  @Override
  public long count(ItemFilterQuery query) {
    return buildQuery(query).count();
  }

  @Transactional
  @Override
  public Item save(Item item) {
    ItemJpaEntity entity =
        item.id() == null ? ItemJpaEntity.fromDomain(item) : findByIdEntity(item.id());
    entity.name = item.name();
    entity.description = item.description();
    entity.category = item.category();
    entity.active = item.active();
    entity.createdAt = item.createdAt().toLocalDateTime();
    if (entity.id == null) {
      panache.persist(entity);
    }
    return entity.toDomain();
  }

  private ItemJpaEntity findByIdEntity(Long id) {
    return panache.find("id", id).firstResult();
  }

  private PanacheQuery<ItemJpaEntity> buildQuery(ItemFilterQuery query) {
    StringBuilder hql = new StringBuilder();
    Object[] params = new Object[0];
    int paramIndex = 1;

    if (query.search() != null && !query.search().isBlank()) {
      hql.append("name ILIKE ?").append(paramIndex).append(" OR description ILIKE ?")
          .append(paramIndex);
      params = new Object[]{"%" + query.search() + "%"};
      paramIndex++;
    }

    if (query.category() != null) {
      if (!hql.isEmpty()) {
        hql.append(" AND ");
      }
      hql.append("category = ?").append(paramIndex);
      params = appendParam(params, query.category().name());
      paramIndex++;
    }

    if (query.active() != null) {
      if (!hql.isEmpty()) {
        hql.append(" AND ");
      }
      hql.append("is_active = ?").append(paramIndex);
      params = appendParam(params, query.active());
      paramIndex++;
    }

    return hql.isEmpty() ? panache.findAll() : panache.find(hql.toString(), params);
  }

  private Object[] appendParam(Object[] params, Object newParam) {
    Object[] newParams = new Object[params.length + 1];
    System.arraycopy(params, 0, newParams, 0, params.length);
    newParams[params.length] = newParam;
    return newParams;
  }
}
