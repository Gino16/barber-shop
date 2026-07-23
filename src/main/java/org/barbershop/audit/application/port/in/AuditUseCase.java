package org.barbershop.audit.application.port.in;

import org.barbershop.audit.application.AuditFilterQuery;
import org.barbershop.audit.application.PagedResponse;
import org.barbershop.audit.domain.AuditLog;

public interface AuditUseCase {
    PagedResponse<AuditLog> list(AuditFilterQuery query);
}
