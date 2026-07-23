package org.barbershop.appointment.adapter.out.persistence;

import org.barbershop.appointment.application.AppointmentFilterQuery;
import org.barbershop.appointment.application.port.out.AppointmentRepositoryPort;
import org.barbershop.appointment.domain.Appointment;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class AppointmentPersistenceAdapter implements AppointmentRepositoryPort {

  @Inject
  AppointmentPanacheRepository repository;

  @Override
  public List<Appointment> find(AppointmentFilterQuery query) {
    return buildQuery(query.withDefaults())
        .range(query.offset(), query.offset() + query.pageSize() - 1)
        .stream()
        .map(AppointmentJpaEntity::toDomain)
        .toList();
  }

  @Override
  public long count(AppointmentFilterQuery query) {
    return buildQuery(query.withDefaults()).count();
  }

  @Override
  public Optional<Appointment> findById(Long id) {
    return repository.find("id", id).firstResultOptional().map(AppointmentJpaEntity::toDomain);
  }

  @Override
  public Appointment save(Appointment appointment) {
    AppointmentJpaEntity entity =
        appointment.id() == null
            ? AppointmentJpaEntity.fromDomain(appointment)
            : findByIdEntity(appointment.id());
    entity.customerId = appointment.customerId();
    entity.employeeId = appointment.employeeId();
    entity.scheduledAt = appointment.scheduledAt().toLocalDateTime();
    entity.notes = appointment.notes();
    entity.status = appointment.status();
    entity.createdAt = appointment.createdAt().toLocalDateTime();
    if (entity.id == null) {
      repository.persist(entity);
    }
    return entity.toDomain();
  }

  @Override
  public void delete(Long id) {
    repository.deleteById(id);
  }

  private AppointmentJpaEntity findByIdEntity(Long id) {
    return repository.find("id", id).firstResult();
  }

  private PanacheQuery<AppointmentJpaEntity> buildQuery(AppointmentFilterQuery query) {
    StringBuilder hql = new StringBuilder();
    Object[] params = new Object[0];
    int paramIndex = 1;

    if (query.startDate() != null) {
      hql.append("CAST(scheduled_at AS date) >= ?").append(paramIndex);
      params = appendParam(params, query.startDate());
      paramIndex++;
    }

    if (query.endDate() != null) {
      if (hql.length() > 0) hql.append(" AND ");
      hql.append("CAST(scheduled_at AS date) <= ?").append(paramIndex);
      params = appendParam(params, query.endDate());
      paramIndex++;
    }

    if (query.employeeId() != null) {
      if (hql.length() > 0) hql.append(" AND ");
      hql.append("employee_id = ?").append(paramIndex);
      params = appendParam(params, query.employeeId());
      paramIndex++;
    }

    if (query.customerId() != null) {
      if (hql.length() > 0) hql.append(" AND ");
      hql.append("customer_id = ?").append(paramIndex);
      params = appendParam(params, query.customerId());
      paramIndex++;
    }

    if (query.status() != null) {
      if (hql.length() > 0) hql.append(" AND ");
      hql.append("status = ?").append(paramIndex);
      params = appendParam(params, query.status());
      paramIndex++;
    }

    PanacheQuery<AppointmentJpaEntity> q =
        hql.length() == 0 ? repository.findAll() : repository.find(hql.toString(), params);
    return q;
  }

  private Object[] appendParam(Object[] params, Object newParam) {
    Object[] newParams = new Object[params.length + 1];
    System.arraycopy(params, 0, newParams, 0, params.length);
    newParams[params.length] = newParam;
    return newParams;
  }
}
