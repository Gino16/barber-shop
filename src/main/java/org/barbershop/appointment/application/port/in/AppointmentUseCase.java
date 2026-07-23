package org.barbershop.appointment.application.port.in;

import org.barbershop.appointment.application.AppointmentCommand;
import org.barbershop.appointment.application.AppointmentFilterQuery;
import org.barbershop.appointment.application.PagedResponse;
import org.barbershop.appointment.domain.Appointment;
import java.util.Optional;

public interface AppointmentUseCase {
    PagedResponse<Appointment> list(AppointmentFilterQuery query);
    Optional<Appointment> findById(Long id);
    Appointment create(AppointmentCommand command);
    Optional<Appointment> update(Long id, AppointmentCommand command);
    Optional<Void> delete(Long id);
}
