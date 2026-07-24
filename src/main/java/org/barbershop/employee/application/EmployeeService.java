package org.barbershop.employee.application;

import org.barbershop.audit.application.AuditLogger;
import org.barbershop.audit.domain.AuditAction;
import org.barbershop.employee.application.port.in.EmployeeUseCase;
import org.barbershop.employee.application.port.out.EmployeeRepositoryPort;
import org.barbershop.employee.domain.Employee;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class EmployeeService implements EmployeeUseCase {

  private final EmployeeRepositoryPort repository;
  private final AuditLogger auditLogger;

  @Inject
  public EmployeeService(EmployeeRepositoryPort repository, AuditLogger auditLogger) {
    this.repository = repository;
    this.auditLogger = auditLogger;
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
    Employee created = repository.save(new Employee(null, command.name(), command.role(), command.phone(),
        command.email(), command.active() == null || command.active(), OffsetDateTime.now(ZoneOffset.UTC)));
    auditLogger.record("EMPLOYEE", created.id(), AuditAction.CREATE, null, values(created));
    return created;
  }

  @Override
  public Optional<Employee> update(Long id, EmployeeCommand command) {
    return repository.findById(id)
        .map(existing -> {
          Employee updated = repository.save(new Employee(existing.id(), command.name(),
              command.role(), command.phone(), command.email(),
              command.active() == null || command.active(), existing.createdAt()));
          auditLogger.record("EMPLOYEE", updated.id(), AuditAction.UPDATE, values(existing), values(updated));
          return updated;
        });
  }

  @Override
  public Optional<Void> delete(Long id) {
    return repository.findById(id)
        .map(existing -> {
          repository.delete(id);
          auditLogger.record("EMPLOYEE", existing.id(), AuditAction.DELETE, values(existing), null);
          return null;
        });
  }

  private Map<String, Object> values(Employee employee) {
    Map<String, Object> values = new LinkedHashMap<>();
    values.put("id", employee.id());
    values.put("name", employee.name());
    values.put("role", employee.role());
    values.put("phone", employee.phone());
    values.put("email", employee.email());
    values.put("active", employee.active());
    values.put("createdAt", employee.createdAt());
    return values;
  }
}
