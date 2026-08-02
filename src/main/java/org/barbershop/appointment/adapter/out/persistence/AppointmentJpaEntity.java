package org.barbershop.appointment.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.barbershop.appointment.domain.Appointment;
import org.barbershop.appointment.domain.AppointmentStatus;

@Entity
@Table(name = "appointments")
public class AppointmentJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(name = "customer_id", nullable = false)
  public Long customerId;

  @Column(name = "employee_id", nullable = false)
  public Long employeeId;

  @Column(name = "scheduled_at", nullable = false)
  public LocalDateTime scheduledAt;

  @Column(columnDefinition = "TEXT")
  public String notes;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  public AppointmentStatus status;

  @Column(name = "created_at", nullable = false)
  public LocalDateTime createdAt;

  public Appointment toDomain() {
    return new Appointment(
        id,
        customerId,
        employeeId,
        scheduledAt.atOffset(ZoneOffset.UTC),
        notes,
        status,
        createdAt.atOffset(ZoneOffset.UTC)
    );
  }

  public static AppointmentJpaEntity fromDomain(Appointment appointment) {
    AppointmentJpaEntity entity = new AppointmentJpaEntity();
    entity.id = appointment.id();
    entity.customerId = appointment.customerId();
    entity.employeeId = appointment.employeeId();
    entity.scheduledAt = appointment.scheduledAt().toLocalDateTime();
    entity.notes = appointment.notes();
    entity.status = appointment.status();
    entity.createdAt = appointment.createdAt().toLocalDateTime();
    return entity;
  }
}
