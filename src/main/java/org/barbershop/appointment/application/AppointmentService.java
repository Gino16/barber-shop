package org.barbershop.appointment.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.barbershop.appointment.application.port.in.AppointmentUseCase;
import org.barbershop.appointment.application.port.out.AppointmentRepositoryPort;
import org.barbershop.appointment.domain.Appointment;
import org.barbershop.appointment.domain.AppointmentStatus;
import org.barbershop.audit.application.AuditLogger;
import org.barbershop.audit.domain.AuditAction;
import org.barbershop.common.pagination.PagedResponse;
import org.barbershop.customer.application.port.out.CustomerRepositoryPort;
import org.barbershop.employee.application.port.out.EmployeeRepositoryPort;

@ApplicationScoped
public class AppointmentService implements AppointmentUseCase {

  private final AppointmentRepositoryPort repository;
  private final CustomerRepositoryPort customerRepository;
  private final EmployeeRepositoryPort employeeRepository;
  private final AuditLogger auditLogger;

  @Inject
  public AppointmentService(
      AppointmentRepositoryPort repository,
      CustomerRepositoryPort customerRepository,
      EmployeeRepositoryPort employeeRepository,
      AuditLogger auditLogger) {
    this.repository = repository;
    this.customerRepository = customerRepository;
    this.employeeRepository = employeeRepository;
    this.auditLogger = auditLogger;
  }

  @Override
  public PagedResponse<Appointment> list(AppointmentFilterQuery query) {
    AppointmentFilterQuery validatedQuery = query.withDefaults();
    var appointments = repository.find(validatedQuery);
    long total = repository.count(validatedQuery);
    return new PagedResponse<>(appointments, validatedQuery.page(), validatedQuery.pageSize(),
        total);
  }

  @Override
  public Optional<Appointment> findById(Long id) {
    return repository.findById(id);
  }

  @Override
  public Appointment create(AppointmentCommand command) {
    validateReferences(command);
    AppointmentStatus status =
        command.status() != null ? command.status() : AppointmentStatus.SCHEDULED;
    Appointment created = repository.save(
        new Appointment(null, command.customerId(), command.employeeId(),
            command.scheduledAt(), command.notes(), status, OffsetDateTime.now(ZoneOffset.UTC)));
    auditLogger.record("APPOINTMENT", created.id(), AuditAction.CREATE, null, values(created));
    return created;
  }

  @Override
  public Optional<Appointment> update(Long id, AppointmentCommand command) {
    return repository.findById(id)
        .map(existing -> {
          validateReferences(command);
          AppointmentStatus status =
              command.status() != null ? command.status() : existing.status();
          Appointment updated = repository.save(new Appointment(existing.id(), command.customerId(),
              command.employeeId(), command.scheduledAt(), command.notes(), status,
              existing.createdAt()));
          auditLogger.record("APPOINTMENT", updated.id(), AuditAction.UPDATE, values(existing),
              values(updated));
          return updated;
        });
  }

  @Override
  public Optional<Void> delete(Long id) {
    return repository.findById(id)
        .map(existing -> {
          repository.delete(id);
          auditLogger.record("APPOINTMENT", existing.id(), AuditAction.DELETE, values(existing),
              null);
          return null;
        });
  }

  private Map<String, Object> values(Appointment appointment) {
    Map<String, Object> values = new LinkedHashMap<>();
    values.put("id", appointment.id());
    values.put("customerId", appointment.customerId());
    values.put("employeeId", appointment.employeeId());
    values.put("scheduledAt", appointment.scheduledAt());
    values.put("notes", appointment.notes());
    values.put("status", appointment.status());
    values.put("createdAt", appointment.createdAt());
    return values;
  }

  private void validateReferences(AppointmentCommand command) {
    if (customerRepository.findById(command.customerId()).isEmpty()) {
      throw new NotFoundException("Customer not found: " + command.customerId());
    }
    if (employeeRepository.findById(command.employeeId()).isEmpty()) {
      throw new NotFoundException("Employee not found: " + command.employeeId());
    }
  }
}
