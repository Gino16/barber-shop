package org.barbershop.report.domain;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record DailyReport(
    Long id,
    LocalDate reportDate,
    Double totalSales,
    Integer totalTransactions,
    Double cashSales,
    Double cardSales,
    Double transferSales,
    Double servicesSales,
    Double productsSales,
    OffsetDateTime generatedAt
) {

}
