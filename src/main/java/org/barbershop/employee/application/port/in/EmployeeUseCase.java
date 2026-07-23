package org.barbershop.employee.application.port.in;

import org.barbershop.employee.application.EmployeeCommand;
import org.barbershop.employee.application.EmployeeFilterQuery;
import org.barbershop.employee.application.PagedResponse;
import org.barbershop.employee.domain.Employee;
import java.util.Optional;

public interface EmployeeUseCase {
    PagedResponse<Employee> list(EmployeeFilterQuery query);
    Optional<Employee> findById(Long id);
    Employee create(EmployeeCommand command);
    Optional<Employee> update(Long id, EmployeeCommand command);
    Optional<Void> delete(Long id);
}
