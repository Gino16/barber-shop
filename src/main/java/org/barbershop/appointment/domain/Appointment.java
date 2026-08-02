package org.barbershop.appointment.domain;

import java.time.OffsetDateTime;

public record Appointment(
    Long id,
    Long customerId,
    Long employeeId,
    OffsetDateTime scheduledAt,
    String notes,
    AppointmentStatus status,
    OffsetDateTime createdAt
) {

}
