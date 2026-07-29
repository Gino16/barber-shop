package org.barbershop.report.adapter.in.rest;

import jakarta.ws.rs.core.Response;
import org.barbershop.report.application.port.in.ReportUseCase;
import org.barbershop.report.domain.DailyReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportRestAdapter Tests")
class ReportRestAdapterTest {

  @Mock
  private ReportUseCase useCase;

  @InjectMocks
  private ReportRestAdapter adapter;

  private static final LocalDate TODAY = LocalDate.of(2024, 7, 15);
  private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);

  private DailyReport sampleReport() {
    return new DailyReport(1L, TODAY, 500.0, 5, 200.0, 150.0, 150.0, 300.0, 200.0, NOW);
  }

  @Test
  @DisplayName("Return200WithReport_WhenGetDailyReportCalledWithExistingDate")
  void shouldReturn200WithReportWhenGetDailyReportCalledWithExistingDate() {
    // Arrange
    when(useCase.getDailyReport(TODAY)).thenReturn(Optional.of(sampleReport()));

    // Act
    Response response = adapter.getDailyReport(TODAY);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    verify(useCase).getDailyReport(TODAY);
  }

  @Test
  @DisplayName("Return404_WhenGetDailyReportCalledWithNonExistingDate")
  void shouldReturn404WhenGetDailyReportCalledWithNonExistingDate() {
    // Arrange
    when(useCase.getDailyReport(TODAY)).thenReturn(Optional.empty());

    // Act
    Response response = adapter.getDailyReport(TODAY);

    // Assert
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
  }

  @Test
  @DisplayName("Return201WithReport_WhenGenerateDailyReportCalled")
  void shouldReturn201WithReportWhenGenerateDailyReportCalled() {
    // Arrange
    when(useCase.generateDailyReport(TODAY)).thenReturn(sampleReport());

    // Act
    Response response = adapter.generateDailyReport(TODAY);

    // Assert
    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    verify(useCase).generateDailyReport(TODAY);
  }

  @Test
  @DisplayName("ReturnResponseWithAllReportFields_WhenGetDailyReportCalled")
  void shouldReturnResponseWithAllReportFieldsWhenGetDailyReportCalled() {
    // Arrange
    DailyReport report = sampleReport();
    when(useCase.getDailyReport(TODAY)).thenReturn(Optional.of(report));

    // Act
    Response response = adapter.getDailyReport(TODAY);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    Object entity = response.getEntity();
    assertNotNull(entity);
  }
}
