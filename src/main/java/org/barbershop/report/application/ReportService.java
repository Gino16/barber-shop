package org.barbershop.report.application;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.barbershop.report.application.port.in.ReportUseCase;
import org.barbershop.report.application.port.out.ReportRepositoryPort;
import org.barbershop.report.domain.DailyReport;

@ApplicationScoped
public class ReportService implements ReportUseCase {

  private final ReportRepositoryPort repository;

  @Inject
  public ReportService(ReportRepositoryPort repository) {
    this.repository = repository;
  }

  @Override
  public Optional<DailyReport> getDailyReport(LocalDate date) {
    return repository.findByDate(date);
  }

  @Override
  public DailyReport generateDailyReport(LocalDate date) {
    return getDailyReport(date).orElseGet(() -> {
      DailyReport report = new DailyReport(
          null,
          date,
          0.0,
          0,
          0.0,
          0.0,
          0.0,
          0.0,
          0.0,
          OffsetDateTime.now(ZoneOffset.UTC)
      );
      return repository.save(report);
    });
  }
}
