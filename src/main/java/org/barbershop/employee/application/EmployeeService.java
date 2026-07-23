package org.barbershop.employee.application;

import org.barbershop.employee.application.port.in.EmployeeUseCase;
import org.barbershop.employee.application.port.out.EmployeeRepositoryPort;
import org.barbershop.employee.domain.Employee;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@ApplicationScoped
public class EmployeeService implements EmployeeUseCase {

  private final EmployeeRepositoryPort repository;

  @Inject
  public EmployeeService(EmployeeRepositoryPort repository) {
    this.repository = repository;
  }

  @Override
  public PagedResponse<Employee> list(EmployeeFilterQuery query) {
    EmployeeFilterQuery validatedQuery = query.withDefaults();
    var employees = repository.find(validatedQuery);
    long total = repository.count(validatedQuery);
    return new PagedResponse<>(employees, validatedQuery.page(), validatedQuery.pageSize(), total);
  }

  @Override
  public Optional<Employee> findById(Long id) {
    return repository.findById(id);
  }

  @Override
  public Employee create(EmployeeCommand command) {
    return repository.save(new Employee(null, command.name(), command.role(), command.phone(),
        command.email(), command.active() == null || command.active(), OffsetDateTime.now(ZoneOffset.UTC)));
  }

  @Override
  public Optional<Employee> update(Long id, EmployeeCommand command) {
    return repository.findById(id)
        .map(existing -> repository.save(new Employee(existing.id(), command.name(),
            command.role(), command.phone(), command.email(),
            command.active() == null || command.active(), existing.createdAt())));
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
