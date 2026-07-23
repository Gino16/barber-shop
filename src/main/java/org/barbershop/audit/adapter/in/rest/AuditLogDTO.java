package org.barbershop.audit.adapter.in.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {
    private Long id;
    private String entityType;
    private Long entityId;
    private String action;
    private Map<String, Object> oldValues;
    private Map<String, Object> newValues;
    private String userName;
    private OffsetDateTime timestamp;
}
