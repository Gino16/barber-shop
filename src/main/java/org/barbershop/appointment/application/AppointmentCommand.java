package org.barbershop.appointment.application;

import java.time.OffsetDateTime;
import org.barbershop.appointment.domain.AppointmentStatus;

public record AppointmentCommand(
    Long customerId,
    Long employeeId,
    OffsetDateTime scheduledAt,
    String notes,
    AppointmentStatus status
) {

}
