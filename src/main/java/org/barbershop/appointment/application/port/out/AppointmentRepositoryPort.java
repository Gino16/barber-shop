package org.barbershop.appointment.application.port.out;

import org.barbershop.appointment.application.AppointmentFilterQuery;
import org.barbershop.appointment.domain.Appointment;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepositoryPort {
    List<Appointment> find(AppointmentFilterQuery query);
    long count(AppointmentFilterQuery query);
    Optional<Appointment> findById(Long id);
    Appointment save(Appointment appointment);
    void delete(Long id);
}
