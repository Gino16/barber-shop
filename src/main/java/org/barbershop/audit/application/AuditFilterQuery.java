package org.barbershop.audit.application;

import org.barbershop.audit.domain.AuditAction;

public record AuditFilterQuery(
    String entityType,
    AuditAction action,
    int page,
    int pageSize
) {

  public int offset() {
    return (page - 1) * pageSize;
  }

  public AuditFilterQuery withDefaults() {
    return new AuditFilterQuery(
        entityType,
        action,
        page > 0 ? page : 1,
        pageSize > 0 && pageSize <= 100 ? pageSize : 10
    );
  }
}
