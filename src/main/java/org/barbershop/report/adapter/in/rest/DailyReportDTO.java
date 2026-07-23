package org.barbershop.report.adapter.in.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyReportDTO {
    private Long id;
    private LocalDate reportDate;
    private Double totalSales;
    private Integer totalTransactions;
    private Double cashSales;
    private Double cardSales;
    private Double transferSales;
    private Double servicesSales;
    private Double productsSales;
    private OffsetDateTime generatedAt;
}
