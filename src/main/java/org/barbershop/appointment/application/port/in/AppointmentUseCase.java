package org.barbershop.appointment.application.port.in;

import java.util.Optional;
import org.barbershop.appointment.application.AppointmentCommand;
import org.barbershop.appointment.application.AppointmentFilterQuery;
import org.barbershop.appointment.domain.Appointment;
import org.barbershop.common.pagination.PagedResponse;

public interface AppointmentUseCase {

  PagedResponse<Appointment> list(AppointmentFilterQuery query);

  Optional<Appointment> findById(Long id);

  Appointment create(AppointmentCommand command);

  Optional<Appointment> update(Long id, AppointmentCommand command);

  Optional<Void> delete(Long id);
}
