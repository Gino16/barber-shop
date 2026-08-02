package org.barbershop.report.adapter.in.rest;

import static org.barbershop.common.utils.Constants.LIMA_ZONE;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import org.barbershop.api.ReportsApi;
import org.barbershop.api.model.DailyReportResponse;
import org.barbershop.report.application.port.in.ReportUseCase;
import org.barbershop.report.domain.DailyReport;

@ApplicationScoped
public class ReportRestAdapter implements ReportsApi {

  private final ReportUseCase useCase;

  @Inject
  public ReportRestAdapter(ReportUseCase useCase) {
    this.useCase = useCase;
  }

  @Override
  public Response getDailyReport(LocalDate date) {
    return useCase.getDailyReport(date)
        .map(report -> Response.ok(toDailyReportResponse(report)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @Override
  public Response generateDailyReport(LocalDate date) {
    DailyReport report = useCase.generateDailyReport(date);
    return Response.status(Response.Status.CREATED)
        .entity(toDailyReportResponse(report)).build();
  }

  private DailyReportResponse toDailyReportResponse(DailyReport report) {
    return DailyReportResponse.builder()
        .id(report.id())
        .reportDate(report.reportDate())
        .totalSales(report.totalSales())
        .totalTransactions(report.totalTransactions())
        .cashSales(report.cashSales())
        .cardSales(report.cardSales())
        .transferSales(report.transferSales())
        .servicesSales(report.servicesSales())
        .productsSales(report.productsSales())
        .generatedAt(report.generatedAt().atZoneSameInstant(LIMA_ZONE).toOffsetDateTime())
        .build();
  }
}
