package org.barbershop.customer.adapter.out.persistence;

import org.barbershop.customer.application.CustomerFilterQuery;
import org.barbershop.customer.application.port.out.CustomerRepositoryPort;
import org.barbershop.customer.domain.Customer;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class CustomerPersistenceAdapter implements CustomerRepositoryPort {

  @Inject
  CustomerPanacheRepository repository;

  @Override
  public List<Customer> find(CustomerFilterQuery query) {
    return buildQuery(query.withDefaults())
        .range(query.offset(), query.offset() + query.pageSize() - 1)
        .stream()
        .map(CustomerJpaEntity::toDomain)
        .toList();
  }

  @Override
  public long count(CustomerFilterQuery query) {
    return buildQuery(query.withDefaults()).count();
  }

  @Override
  public Optional<Customer> findById(Long id) {
    return repository.find("id", id).firstResultOptional().map(CustomerJpaEntity::toDomain);
  }

  @Override
  public Customer save(Customer customer) {
    CustomerJpaEntity entity =
        customer.id() == null
            ? CustomerJpaEntity.fromDomain(customer)
            : findByIdEntity(customer.id());
    entity.name = customer.name();
    entity.phone = customer.phone();
    entity.email = customer.email();
    entity.address = customer.address();
    entity.createdAt = customer.createdAt().toLocalDateTime();
    if (entity.id == null) {
      repository.persist(entity);
    }
    return entity.toDomain();
  }

  @Override
  public void delete(Long id) {
    repository.deleteById(id);
  }

  private CustomerJpaEntity findByIdEntity(Long id) {
    return repository.find("id", id).firstResult();
  }

  private PanacheQuery<CustomerJpaEntity> buildQuery(CustomerFilterQuery query) {
    StringBuilder hql = new StringBuilder();
    Object[] params = new Object[0];
    int paramIndex = 1;

    if (query.search() != null && !query.search().isBlank()) {
      hql.append("name ILIKE ?").append(paramIndex).append(" OR phone ILIKE ?").append(paramIndex)
          .append(" OR email ILIKE ?").append(paramIndex);
      params = new Object[] {"%" + query.search() + "%"};
      paramIndex++;
    }

    PanacheQuery<CustomerJpaEntity> q =
        hql.length() == 0 ? repository.findAll() : repository.find(hql.toString(), params);
    return q;
  }
}
