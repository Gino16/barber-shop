package org.barbershop.sale.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SalePanacheRepository implements PanacheRepository<SaleJpaEntity> {

}
