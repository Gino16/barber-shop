package org.barbershop.audit.application;

import org.barbershop.audit.application.port.in.AuditUseCase;
import org.barbershop.audit.application.port.out.AuditRepositoryPort;
import org.barbershop.audit.domain.AuditLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuditService implements AuditUseCase {

  private final AuditRepositoryPort repository;

  @Inject
  public AuditService(AuditRepositoryPort repository) {
    this.repository = repository;
  }

  @Override
  public PagedResponse<AuditLog> list(AuditFilterQuery query) {
    AuditFilterQuery validatedQuery = query.withDefaults();
    var logs = repository.find(validatedQuery);
    long total = repository.count(validatedQuery);
    return new PagedResponse<>(logs, validatedQuery.page(), validatedQuery.pageSize(), total);
  }
}
