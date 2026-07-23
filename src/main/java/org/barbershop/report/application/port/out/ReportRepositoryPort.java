package org.barbershop.report.application.port.out;

import org.barbershop.report.domain.DailyReport;
import java.time.LocalDate;
import java.util.Optional;

public interface ReportRepositoryPort {
    Optional<DailyReport> findByDate(LocalDate date);
    DailyReport save(DailyReport report);
}
