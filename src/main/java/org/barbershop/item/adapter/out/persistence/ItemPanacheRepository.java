package org.barbershop.item.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
class ItemPanacheRepository implements PanacheRepository<ItemJpaEntity> {

}
