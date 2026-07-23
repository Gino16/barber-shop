package org.barbershop.employee.application;

import org.barbershop.employee.domain.EmployeeRole;

public record EmployeeFilterQuery(
    String search,
    EmployeeRole role,
    Boolean active,
    int page,
    int pageSize
) {

  public int offset() {
    return (page - 1) * pageSize;
  }

  public EmployeeFilterQuery withDefaults() {
    return new EmployeeFilterQuery(
        search != null ? search.trim() : null,
        role,
        active,
        page > 0 ? page : 1,
        pageSize > 0 && pageSize <= 100 ? pageSize : 10
    );
  }
}
