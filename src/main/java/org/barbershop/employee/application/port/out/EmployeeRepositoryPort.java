package org.barbershop.employee.application.port.out;

import java.util.List;
import java.util.Optional;
import org.barbershop.employee.application.EmployeeFilterQuery;
import org.barbershop.employee.domain.Employee;

public interface EmployeeRepositoryPort {

  List<Employee> find(EmployeeFilterQuery query);

  long count(EmployeeFilterQuery query);

  Optional<Employee> findById(Long id);

  Employee save(Employee employee);

  void delete(Long id);
}
