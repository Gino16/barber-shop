package org.barbershop.appointment.application;

import org.barbershop.appointment.domain.AppointmentStatus;
import java.time.OffsetDateTime;

public record AppointmentCommand(
    Long customerId,
    Long employeeId,
    OffsetDateTime scheduledAt,
    String notes,
    AppointmentStatus status
) {
}
