package org.barbershop.customer.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.barbershop.audit.application.AuditLogger;
import org.barbershop.audit.domain.AuditAction;
import org.barbershop.common.pagination.PagedResponse;
import org.barbershop.customer.application.port.in.CustomerUseCase;
import org.barbershop.customer.application.port.out.CustomerRepositoryPort;
import org.barbershop.customer.domain.Customer;

@ApplicationScoped
public class CustomerService implements CustomerUseCase {

  private final CustomerRepositoryPort repository;
  private final AuditLogger auditLogger;

  @Inject
  public CustomerService(CustomerRepositoryPort repository, AuditLogger auditLogger) {
    this.repository = repository;
    this.auditLogger = auditLogger;
  }

  @Override
  public PagedResponse<Customer> list(CustomerFilterQuery query) {
    CustomerFilterQuery validatedQuery = query.withDefaults();
    var customers = repository.find(validatedQuery);
    long total = repository.count(validatedQuery);
    return new PagedResponse<>(customers, validatedQuery.page(), validatedQuery.pageSize(), total);
  }

  @Override
  public Optional<Customer> findById(Long id) {
    return repository.findById(id);
  }

  @Override
  public Customer create(CustomerCommand command) {
    Customer created = repository.save(
        new Customer(null, command.name(), command.phone(), command.email(),
            command.address(), OffsetDateTime.now(ZoneOffset.UTC)));
    auditLogger.record("CUSTOMER", created.id(), AuditAction.CREATE, null, values(created));
    return created;
  }

  @Override
  public Optional<Customer> update(Long id, CustomerCommand command) {
    return repository.findById(id)
        .map(existing -> {
          Customer updated = repository.save(new Customer(existing.id(), command.name(),
              command.phone(), command.email(), command.address(), existing.createdAt()));
          auditLogger.record("CUSTOMER", updated.id(), AuditAction.UPDATE, values(existing),
              values(updated));
          return updated;
        });
  }

  @Override
  public Optional<Void> delete(Long id) {
    return repository.findById(id)
        .map(existing -> {
          repository.delete(id);
          auditLogger.record("CUSTOMER", existing.id(), AuditAction.DELETE, values(existing), null);
          return null;
        });
  }

  private Map<String, Object> values(Customer customer) {
    Map<String, Object> values = new LinkedHashMap<>();
    values.put("id", customer.id());
    values.put("name", customer.name());
    values.put("phone", customer.phone());
    values.put("email", customer.email());
    values.put("address", customer.address());
    values.put("createdAt", customer.createdAt());
    return values;
  }
}
