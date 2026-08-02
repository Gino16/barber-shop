package org.barbershop.audit.domain;

import java.time.OffsetDateTime;
import java.util.Map;

public record AuditLog(
    Long id,
    String entityType,
    Long entityId,
    AuditAction action,
    Map<String, Object> oldValues,
    Map<String, Object> newValues,
    String userName,
    OffsetDateTime timestamp
) {

}
