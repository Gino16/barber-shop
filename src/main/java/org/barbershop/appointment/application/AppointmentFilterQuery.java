package org.barbershop.appointment.application;

import org.barbershop.appointment.domain.AppointmentStatus;
import java.time.LocalDate;

public record AppointmentFilterQuery(
    LocalDate startDate,
    LocalDate endDate,
    Long employeeId,
    Long customerId,
    AppointmentStatus status,
    int page,
    int pageSize
) {

  public int offset() {
    return (page - 1) * pageSize;
  }

  public AppointmentFilterQuery withDefaults() {
    return new AppointmentFilterQuery(
        startDate,
        endDate,
        employeeId,
        customerId,
        status,
        page > 0 ? page : 1,
        pageSize > 0 && pageSize <= 100 ? pageSize : 10
    );
  }
}
