package org.barbershop.audit.application.port.out;

import org.barbershop.audit.application.AuditFilterQuery;
import org.barbershop.audit.domain.AuditLog;
import java.util.List;

public interface AuditRepositoryPort {
    List<AuditLog> find(AuditFilterQuery query);
    long count(AuditFilterQuery query);
    AuditLog save(AuditLog log);
}
