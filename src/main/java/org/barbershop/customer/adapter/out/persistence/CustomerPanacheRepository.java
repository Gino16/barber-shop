package org.barbershop.customer.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomerPanacheRepository implements PanacheRepository<CustomerJpaEntity> {

}
