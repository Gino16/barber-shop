package org.barbershop.report.application.port.out;

import java.time.LocalDate;
import java.util.Optional;
import org.barbershop.report.domain.DailyReport;

public interface ReportRepositoryPort {

  Optional<DailyReport> findByDate(LocalDate date);

  DailyReport save(DailyReport report);
}
