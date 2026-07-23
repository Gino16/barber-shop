package org.barbershop.report.application.port.in;

import org.barbershop.report.domain.DailyReport;
import java.time.LocalDate;
import java.util.Optional;

public interface ReportUseCase {
    Optional<DailyReport> getDailyReport(LocalDate date);
    DailyReport generateDailyReport(LocalDate date);
}
