package org.barbershop.report.application.port.in;

import java.time.LocalDate;
import java.util.Optional;
import org.barbershop.report.domain.DailyReport;

public interface ReportUseCase {

  Optional<DailyReport> getDailyReport(LocalDate date);

  DailyReport generateDailyReport(LocalDate date);
}
