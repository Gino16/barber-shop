package org.barbershop.employee.application;

import org.barbershop.employee.domain.EmployeeRole;

public record EmployeeCommand(String name, EmployeeRole role, String phone, String email, Boolean active) {
}
