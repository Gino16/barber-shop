package org.barbershop.customer.adapter.out.persistence;

import org.barbershop.customer.domain.Customer;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "customers")
public class CustomerJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(nullable = false, length = 100)
  public String name;

  @Column(nullable = false, length = 20)
  public String phone;

  @Column(nullable = false, length = 100)
  public String email;

  @Column(length = 200)
  public String address;

  @Column(name = "created_at", nullable = false)
  public LocalDateTime createdAt;

  public Customer toDomain() {
    return new Customer(
        id,
        name,
        phone,
        email,
        address,
        createdAt.atOffset(ZoneOffset.UTC)
    );
  }

  public static CustomerJpaEntity fromDomain(Customer customer) {
    CustomerJpaEntity entity = new CustomerJpaEntity();
    entity.id = customer.id();
    entity.name = customer.name();
    entity.phone = customer.phone();
    entity.email = customer.email();
    entity.address = customer.address();
    entity.createdAt = customer.createdAt().toLocalDateTime();
    return entity;
  }
}
