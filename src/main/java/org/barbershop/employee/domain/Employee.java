package org.barbershop.employee.domain;

import java.time.OffsetDateTime;

public record Employee(
    Long id,
    String name,
    EmployeeRole role,
    String phone,
    String email,
    boolean active,
    OffsetDateTime createdAt
) {
}
