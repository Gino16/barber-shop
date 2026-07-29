package org.barbershop.report.adapter.out.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;
import org.barbershop.report.application.port.out.ReportRepositoryPort;
import org.barbershop.report.domain.DailyReport;

@ApplicationScoped
@Transactional
public class ReportPersistenceAdapter implements ReportRepositoryPort {

  @Inject
  ReportPanacheRepository repository;

  @Override
  public Optional<DailyReport> findByDate(LocalDate date) {
    return repository
        .find("reportDate", date)
        .firstResultOptional()
        .map(DailyReportJpaEntity::toDomain);
  }

  @Override
  public DailyReport save(DailyReport report) {
    DailyReportJpaEntity entity =
        report.id() == null
            ? DailyReportJpaEntity.fromDomain(report)
            : findByIdEntity(report.id());
    entity.reportDate = report.reportDate();
    entity.totalSales = report.totalSales();
    entity.totalTransactions = report.totalTransactions();
    entity.cashSales = report.cashSales();
    entity.cardSales = report.cardSales();
    entity.transferSales = report.transferSales();
    entity.servicesSales = report.servicesSales();
    entity.productsSales = report.productsSales();
    entity.generatedAt = report.generatedAt().toLocalDateTime();

    if (entity.id == null) {
      repository.persist(entity);
    }

    return entity.toDomain();
  }

  private DailyReportJpaEntity findByIdEntity(Long id) {
    return repository.find("id", id).firstResult();
  }
}
