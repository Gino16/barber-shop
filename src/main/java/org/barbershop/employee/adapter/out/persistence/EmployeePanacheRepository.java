package org.barbershop.employee.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmployeePanacheRepository implements PanacheRepository<EmployeeJpaEntity> {

}
