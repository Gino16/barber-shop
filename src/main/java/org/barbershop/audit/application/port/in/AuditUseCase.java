package org.barbershop.audit.application.port.in;

import org.barbershop.audit.application.AuditFilterQuery;
import org.barbershop.audit.domain.AuditLog;
import org.barbershop.common.pagination.PagedResponse;

public interface AuditUseCase {

  PagedResponse<AuditLog> list(AuditFilterQuery query);
}
