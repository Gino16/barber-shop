package org.barbershop.report.adapter.out.persistence;

import org.barbershop.report.domain.DailyReport;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "daily_reports")
public class DailyReportJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(name = "report_date", nullable = false, unique = true)
  public LocalDate reportDate;

  @Column(name = "total_sales", nullable = false)
  public Double totalSales;

  @Column(name = "total_transactions", nullable = false)
  public Integer totalTransactions;

  @Column(name = "cash_sales", nullable = false)
  public Double cashSales;

  @Column(name = "card_sales", nullable = false)
  public Double cardSales;

  @Column(name = "transfer_sales", nullable = false)
  public Double transferSales;

  @Column(name = "services_sales", nullable = false)
  public Double servicesSales;

  @Column(name = "products_sales", nullable = false)
  public Double productsSales;

  @Column(name = "created_at", nullable = false)
  public LocalDateTime generatedAt;

  public DailyReport toDomain() {
    return new DailyReport(
        id,
        reportDate,
        totalSales,
        totalTransactions,
        cashSales,
        cardSales,
        transferSales,
        servicesSales,
        productsSales,
        generatedAt.atOffset(ZoneOffset.UTC)
    );
  }

  public static DailyReportJpaEntity fromDomain(DailyReport report) {
    DailyReportJpaEntity entity = new DailyReportJpaEntity();
    entity.id = report.id();
    entity.reportDate = report.reportDate();
    entity.totalSales = report.totalSales();
    entity.totalTransactions = report.totalTransactions();
    entity.cashSales = report.cashSales();
    entity.cardSales = report.cardSales();
    entity.transferSales = report.transferSales();
    entity.servicesSales = report.servicesSales();
    entity.productsSales = report.productsSales();
    entity.generatedAt = report.generatedAt().toLocalDateTime();
    return entity;
  }
}
