package org.barbershop.report.adapter.in.rest;

import org.barbershop.report.application.port.in.ReportUseCase;
import org.barbershop.report.domain.DailyReport;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;

@ApplicationScoped
@Path("/reports")
@Produces("application/json")
public class ReportRestAdapter {

  private final ReportUseCase useCase;

  @Inject
  public ReportRestAdapter(ReportUseCase useCase) {
    this.useCase = useCase;
  }

  @GET
  @Path("/daily/{date}")
  public Response getDailyReport(@PathParam("date") String date) {
    LocalDate reportDate = LocalDate.parse(date);
    return useCase.getDailyReport(reportDate)
        .map(report -> Response.ok(toDTO(report)).build())
        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @POST
  @Path("/daily/generate/{date}")
  public Response generateDailyReport(@PathParam("date") String date) {
    LocalDate reportDate = LocalDate.parse(date);
    DailyReport report = useCase.generateDailyReport(reportDate);
    return Response.status(Response.Status.CREATED)
        .entity(toDTO(report)).build();
  }

  private DailyReportDTO toDTO(DailyReport report) {
    return new DailyReportDTO(report.id(), report.reportDate(), report.totalSales(),
        report.totalTransactions(), report.cashSales(), report.cardSales(),
        report.transferSales(), report.servicesSales(), report.productsSales(),
        report.generatedAt());
  }
}
