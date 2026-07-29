package org.barbershop.audit.adapter.out.persistence;

import org.barbershop.audit.application.AuditFilterQuery;
import org.barbershop.audit.application.port.out.AuditRepositoryPort;
import org.barbershop.audit.domain.AuditLog;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.ZoneOffset;
import java.util.List;

@ApplicationScoped
@Transactional
public class AuditPersistenceAdapter implements AuditRepositoryPort {

  @Inject
  AuditPanacheRepository repository;

  @Override
  public List<AuditLog> find(AuditFilterQuery query) {
    return buildQuery(query.withDefaults())
        .range(query.offset(), query.offset() + query.pageSize() - 1)
        .stream()
        .map(this::toDomain)
        .toList();
  }

  @Override
  public long count(AuditFilterQuery query) {
    return buildQuery(query.withDefaults()).count();
  }

  @Override
  public AuditLog save(AuditLog log) {
    AuditLogJpaEntity entity = new AuditLogJpaEntity();
    entity.id = log.id();
    entity.entityType = log.entityType();
    entity.entityId = log.entityId();
    entity.action = log.action();
    entity.setOldValues(log.oldValues());
    entity.setNewValues(log.newValues());
    entity.userName = log.userName();
    entity.timestamp = log.timestamp().toLocalDateTime();

    if (entity.id == null) {
      repository.persist(entity);
    }

    return toDomain(entity);
  }

  private PanacheQuery<AuditLogJpaEntity> buildQuery(AuditFilterQuery query) {
    StringBuilder hql = new StringBuilder();
    Object[] params = new Object[0];
    int paramIndex = 1;

    if (query.entityType() != null && !query.entityType().isBlank()) {
      hql.append("entityType = ?").append(paramIndex);
      params = appendParam(params, query.entityType());
      paramIndex++;
    }

    if (query.action() != null) {
      if (!hql.isEmpty()) hql.append(" AND ");
      hql.append("action = ?").append(paramIndex);
      params = appendParam(params, query.action());
      paramIndex++;
    }

    PanacheQuery<AuditLogJpaEntity> q =
        hql.isEmpty() ?  repository.findAll() : repository.find(hql.toString(), params);
    return q;
  }

  private AuditLog toDomain(AuditLogJpaEntity entity) {
    return new AuditLog(
        entity.id,
        entity.entityType,
        entity.entityId,
        entity.action,
        entity.getOldValues(),
        entity.getNewValues(),
        entity.userName,
        entity.timestamp.atOffset(ZoneOffset.UTC)
    );
  }

  private Object[] appendParam(Object[] params, Object newParam) {
    Object[] newParams = new Object[params.length + 1];
    System.arraycopy(params, 0, newParams, 0, params.length);
    newParams[params.length] = newParam;
    return newParams;
  }
}
