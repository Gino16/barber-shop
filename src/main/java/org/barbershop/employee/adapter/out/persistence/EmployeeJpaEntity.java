package org.barbershop.employee.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.barbershop.employee.domain.Employee;
import org.barbershop.employee.domain.EmployeeRole;

@Entity
@Table(name = "employees")
public class EmployeeJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(nullable = false, length = 100)
  public String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  public EmployeeRole role;

  @Column(nullable = false, length = 20)
  public String phone;

  @Column(nullable = false, length = 100)
  public String email;

  @Column(name = "is_active", nullable = false)
  public boolean active = true;

  @Column(name = "created_at", nullable = false)
  public LocalDateTime createdAt;

  public Employee toDomain() {
    return new Employee(
        id,
        name,
        role,
        phone,
        email,
        active,
        createdAt.atOffset(ZoneOffset.UTC)
    );
  }

  public static EmployeeJpaEntity fromDomain(Employee employee) {
    EmployeeJpaEntity entity = new EmployeeJpaEntity();
    entity.id = employee.id();
    entity.name = employee.name();
    entity.role = employee.role();
    entity.phone = employee.phone();
    entity.email = employee.email();
    entity.active = employee.active();
    entity.createdAt = employee.createdAt().toLocalDateTime();
    return entity;
  }
}
