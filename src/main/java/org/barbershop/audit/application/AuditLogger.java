package org.barbershop.audit.application;

import org.barbershop.audit.application.port.out.AuditRepositoryPort;
import org.barbershop.audit.domain.AuditAction;
import org.barbershop.audit.domain.AuditLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@ApplicationScoped
public class AuditLogger {

  private static final String SYSTEM_USER = "system";

  private final AuditRepositoryPort repository;

  @Inject
  public AuditLogger(AuditRepositoryPort repository) {
    this.repository = repository;
  }

  public void record(
      String entityType,
      Long entityId,
      AuditAction action,
      Map<String, Object> oldValues,
      Map<String, Object> newValues) {
    repository.save(new AuditLog(
        null,
        entityType,
        entityId,
        action,
        oldValues,
        newValues,
        SYSTEM_USER,
        OffsetDateTime.now(ZoneOffset.UTC)
    ));
  }
}
