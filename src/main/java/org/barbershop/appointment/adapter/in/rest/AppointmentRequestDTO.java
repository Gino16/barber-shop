package org.barbershop.appointment.adapter.in.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDTO {
    private Long customerId;
    private Long employeeId;
    private OffsetDateTime scheduledAt;
    private String notes;
    private String status;
}
