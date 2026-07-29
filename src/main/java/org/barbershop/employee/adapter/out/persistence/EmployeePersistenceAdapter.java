package org.barbershop.employee.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.barbershop.employee.application.EmployeeFilterQuery;
import org.barbershop.employee.application.port.out.EmployeeRepositoryPort;
import org.barbershop.employee.domain.Employee;

@ApplicationScoped
@Transactional
public class EmployeePersistenceAdapter implements EmployeeRepositoryPort {

  @Inject
  EmployeePanacheRepository repository;

  @Override
  public List<Employee> find(EmployeeFilterQuery query) {
    return buildQuery(query.withDefaults())
        .range(query.offset(), query.offset() + query.pageSize() - 1)
        .stream()
        .map(EmployeeJpaEntity::toDomain)
        .toList();
  }

  @Override
  public long count(EmployeeFilterQuery query) {
    return buildQuery(query.withDefaults()).count();
  }

  @Override
  public Optional<Employee> findById(Long id) {
    return repository.find("id", id).firstResultOptional().map(EmployeeJpaEntity::toDomain);
  }

  @Override
  public Employee save(Employee employee) {
    EmployeeJpaEntity entity =
        employee.id() == null
            ? EmployeeJpaEntity.fromDomain(employee)
            : findByIdEntity(employee.id());
    entity.name = employee.name();
    entity.role = employee.role();
    entity.phone = employee.phone();
    entity.email = employee.email();
    entity.active = employee.active();
    entity.createdAt = employee.createdAt().toLocalDateTime();
    if (entity.id == null) {
      repository.persist(entity);
    }
    return entity.toDomain();
  }

  @Override
  public void delete(Long id) {
    repository.deleteById(id);
  }

  private EmployeeJpaEntity findByIdEntity(Long id) {
    return repository.find("id", id).firstResult();
  }

  private PanacheQuery<EmployeeJpaEntity> buildQuery(EmployeeFilterQuery query) {
    StringBuilder hql = new StringBuilder();
    Object[] params = new Object[0];
    int paramIndex = 1;

    if (query.search() != null && !query.search().isBlank()) {
      hql.append("name ILIKE ?").append(paramIndex).append(" OR phone ILIKE ?").append(paramIndex)
          .append(" OR email ILIKE ?").append(paramIndex);
      params = new Object[]{"%" + query.search() + "%"};
      paramIndex++;
    }

    if (query.role() != null) {
      if (!hql.isEmpty()) {
        hql.append(" AND ");
      }
      hql.append("role = ?").append(paramIndex);
      params = appendParam(params, query.role());
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

    return hql.isEmpty() ? repository.findAll() : repository.find(hql.toString(), params);
  }

  private Object[] appendParam(Object[] params, Object newParam) {
    Object[] newParams = new Object[params.length + 1];
    System.arraycopy(params, 0, newParams, 0, params.length);
    newParams[params.length] = newParam;
    return newParams;
  }
}
