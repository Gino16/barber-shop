package org.barbershop.customer.application;

import org.barbershop.customer.application.port.in.CustomerUseCase;
import org.barbershop.customer.application.port.out.CustomerRepositoryPort;
import org.barbershop.customer.domain.Customer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@ApplicationScoped
public class CustomerService implements CustomerUseCase {

  private final CustomerRepositoryPort repository;

  @Inject
  public CustomerService(CustomerRepositoryPort repository) {
    this.repository = repository;
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
    return repository.save(new Customer(null, command.name(), command.phone(), command.email(),
        command.address(), OffsetDateTime.now(ZoneOffset.UTC)));
  }

  @Override
  public Optional<Customer> update(Long id, CustomerCommand command) {
    return repository.findById(id)
        .map(existing -> repository.save(new Customer(existing.id(), command.name(),
            command.phone(), command.email(), command.address(), existing.createdAt())));
  }

  @Override
  public Optional<Void> delete(Long id) {
    return repository.findById(id)
        .map(existing -> {
          repository.delete(id);
          return null;
        });
  }
}
