package org.barbershop.appointment.application;

import org.barbershop.appointment.application.port.in.AppointmentUseCase;
import org.barbershop.appointment.application.port.out.AppointmentRepositoryPort;
import org.barbershop.appointment.domain.Appointment;
import org.barbershop.appointment.domain.AppointmentStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@ApplicationScoped
public class AppointmentService implements AppointmentUseCase {

  private final AppointmentRepositoryPort repository;

  @Inject
  public AppointmentService(AppointmentRepositoryPort repository) {
    this.repository = repository;
  }

  @Override
  public PagedResponse<Appointment> list(AppointmentFilterQuery query) {
    AppointmentFilterQuery validatedQuery = query.withDefaults();
    var appointments = repository.find(validatedQuery);
    long total = repository.count(validatedQuery);
    return new PagedResponse<>(appointments, validatedQuery.page(), validatedQuery.pageSize(), total);
  }

  @Override
  public Optional<Appointment> findById(Long id) {
    return repository.findById(id);
  }

  @Override
  public Appointment create(AppointmentCommand command) {
    AppointmentStatus status = command.status() != null ? command.status() : AppointmentStatus.SCHEDULED;
    return repository.save(new Appointment(null, command.customerId(), command.employeeId(),
        command.scheduledAt(), command.notes(), status, OffsetDateTime.now(ZoneOffset.UTC)));
  }

  @Override
  public Optional<Appointment> update(Long id, AppointmentCommand command) {
    return repository.findById(id)
        .map(existing -> {
          AppointmentStatus status = command.status() != null ? command.status() : existing.status();
          return repository.save(new Appointment(existing.id(), command.customerId(),
              command.employeeId(), command.scheduledAt(), command.notes(), status, existing.createdAt()));
        });
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
