package org.barbershop.audit.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuditPanacheRepository implements PanacheRepository<AuditLogJpaEntity> {

}
