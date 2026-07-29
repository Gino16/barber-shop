package org.barbershop.report.application;

import org.barbershop.report.application.port.out.ReportRepositoryPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService Tests")
class ReportServiceTest {

  @Mock
  private ReportRepositoryPort repository;

  @InjectMocks
  private ReportService reportService;

  private static final LocalDate TODAY = LocalDate.of(2024, 7, 15);
  private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);

  private DailyReport sampleReport(Long id) {
    return new DailyReport(id, TODAY, 500.0, 5, 200.0, 150.0, 150.0, 300.0, 200.0, NOW);
  }

  @Test
  @DisplayName("ReturnReport_WhenGetDailyReportCalledWithExistingDate")
  void shouldReturnReportWhenGetDailyReportCalledWithExistingDate() {
    // Arrange
    when(repository.findByDate(TODAY)).thenReturn(Optional.of(sampleReport(1L)));

    // Act
    Optional<DailyReport> result = reportService.getDailyReport(TODAY);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(1L, result.get().id());
    assertEquals(TODAY, result.get().reportDate());
    assertEquals(500.0, result.get().totalSales());
    verify(repository).findByDate(TODAY);
  }

  @Test
  @DisplayName("ReturnEmpty_WhenGetDailyReportCalledWithNonExistingDate")
  void shouldReturnEmptyWhenGetDailyReportCalledWithNonExistingDate() {
    // Arrange
    when(repository.findByDate(TODAY)).thenReturn(Optional.empty());

    // Act
    Optional<DailyReport> result = reportService.getDailyReport(TODAY);

    // Assert
    assertTrue(result.isEmpty());
    verify(repository, never()).save(any());
  }

  @Test
  @DisplayName("ReturnExistingReport_WhenGenerateDailyReportCalledWithExistingDate")
  void shouldReturnExistingReportWhenGenerateDailyReportCalledWithExistingDate() {
    // Arrange
    DailyReport existing = sampleReport(1L);
    when(repository.findByDate(TODAY)).thenReturn(Optional.of(existing));

    // Act
    DailyReport result = reportService.generateDailyReport(TODAY);

    // Assert
    assertNotNull(result);
    assertEquals(1L, result.id());
    verify(repository, never()).save(any());
  }

  @Test
  @DisplayName("CreateAndSaveNewReport_WhenGenerateDailyReportCalledWithNonExistingDate")
  void shouldCreateAndSaveNewReportWhenGenerateDailyReportCalledWithNonExistingDate() {
    // Arrange
    DailyReport newReport = sampleReport(2L);
    when(repository.findByDate(TODAY)).thenReturn(Optional.empty());
    when(repository.save(any(DailyReport.class))).thenReturn(newReport);

    // Act
    DailyReport result = reportService.generateDailyReport(TODAY);

    // Assert
    assertNotNull(result);
    assertEquals(2L, result.id());
    verify(repository).save(any(DailyReport.class));
  }

  @Test
  @DisplayName("SaveReportWithZeroValues_WhenGenerateNewReport")
  void shouldSaveReportWithZeroValuesWhenGenerateNewReport() {
    // Arrange
    when(repository.findByDate(TODAY)).thenReturn(Optional.empty());
    when(repository.save(any(DailyReport.class))).thenAnswer(inv -> inv.getArgument(0));

    // Act
    DailyReport result = reportService.generateDailyReport(TODAY);

    // Assert
    assertNull(result.id());
    assertEquals(0.0, result.totalSales());
    assertEquals(0, result.totalTransactions());
    assertEquals(TODAY, result.reportDate());
  }
}
