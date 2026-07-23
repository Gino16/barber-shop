package org.barbershop.employee.adapter.in.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {
    private Long id;
    private String name;
    private String role;
    private String phone;
    private String email;
    private Boolean active;
    private OffsetDateTime createdAt;
}
