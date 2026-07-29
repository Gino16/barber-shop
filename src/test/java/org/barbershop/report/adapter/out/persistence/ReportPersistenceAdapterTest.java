package org.barbershop.report.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.barbershop.report.domain.DailyReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportPersistenceAdapter Tests")
class ReportPersistenceAdapterTest {

  @Mock
  private ReportPanacheRepository repository;

  @InjectMocks
  private ReportPersistenceAdapter adapter;

  private static final LocalDate TODAY = LocalDate.of(2024, 7, 15);
  private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);

  private DailyReportJpaEntity sampleEntity(Long id) {
    DailyReportJpaEntity entity = new DailyReportJpaEntity();
    entity.id = id;
    entity.reportDate = TODAY;
    entity.totalSales = 500.0;
    entity.totalTransactions = 5;
    entity.cashSales = 200.0;
    entity.cardSales = 150.0;
    entity.transferSales = 150.0;
    entity.servicesSales = 300.0;
    entity.productsSales = 200.0;
    entity.generatedAt = LocalDateTime.now();
    return entity;
  }

  @Test
  @DisplayName("ReturnReport_WhenFindByDateCalledWithExistingDate")
  @SuppressWarnings("unchecked")
  void shouldReturnReportWhenFindByDateCalledWithExistingDate() {
    // Arrange
    DailyReportJpaEntity entity = sampleEntity(1L);
    PanacheQuery<DailyReportJpaEntity> mockQuery = mock(PanacheQuery.class);
    when(repository.find("reportDate", TODAY)).thenReturn(mockQuery);
    when(mockQuery.firstResultOptional()).thenReturn(Optional.of(entity));

    // Act
    Optional<DailyReport> result = adapter.findByDate(TODAY);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(1L, result.get().id());
    assertEquals(TODAY, result.get().reportDate());
    assertEquals(500.0, result.get().totalSales());
  }

  @Test
  @DisplayName("ReturnEmpty_WhenFindByDateCalledWithNonExistingDate")
  @SuppressWarnings("unchecked")
  void shouldReturnEmptyWhenFindByDateCalledWithNonExistingDate() {
    // Arrange
    PanacheQuery<DailyReportJpaEntity> mockQuery = mock(PanacheQuery.class);
    when(repository.find("reportDate", TODAY)).thenReturn(mockQuery);
    when(mockQuery.firstResultOptional()).thenReturn(Optional.empty());

    // Act
    Optional<DailyReport> result = adapter.findByDate(TODAY);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("PersistEntity_WhenSaveCalledWithNewReport")
  void shouldPersistEntityWhenSaveCalledWithNewReport() {
    // Arrange
    DailyReport newReport = new DailyReport(null, TODAY, 500.0, 5, 200.0, 150.0, 150.0, 300.0, 200.0, NOW);
    // Panache void methods are no-ops by default in mocks — no stubbing needed

    // Act
    DailyReport result = adapter.save(newReport);

    // Assert
    assertNotNull(result);
    assertEquals(TODAY, result.reportDate());
    assertEquals(500.0, result.totalSales());
  }

  @Test
  @DisplayName("UpdateEntity_WhenSaveCalledWithExistingReport")
  @SuppressWarnings("unchecked")
  void shouldUpdateEntityWhenSaveCalledWithExistingReport() {
    // Arrange
    DailyReport existingReport = new DailyReport(1L, TODAY, 600.0, 6, 250.0, 200.0, 150.0, 350.0, 250.0, NOW);
    DailyReportJpaEntity existingEntity = sampleEntity(1L);
    PanacheQuery<DailyReportJpaEntity> mockQuery = mock(PanacheQuery.class);
    when(repository.find("id", 1L)).thenReturn(mockQuery);
    when(mockQuery.firstResult()).thenReturn(existingEntity);

    // Act
    DailyReport result = adapter.save(existingReport);

    // Assert
    assertNotNull(result);
    assertEquals(600.0, result.totalSales());
  }

  @Test
  @DisplayName("ConvertJpaEntityToDomain_WhenToDomainCalled")
  void shouldConvertJpaEntityToDomainWhenToDomainCalled() {
    // Arrange
    DailyReportJpaEntity entity = sampleEntity(1L);

    // Act
    DailyReport domain = entity.toDomain();

    // Assert
    assertEquals(1L, domain.id());
    assertEquals(TODAY, domain.reportDate());
    assertEquals(500.0, domain.totalSales());
    assertEquals(5, domain.totalTransactions());
  }

  @Test
  @DisplayName("ConvertDomainToJpaEntity_WhenFromDomainCalled")
  void shouldConvertDomainToJpaEntityWhenFromDomainCalled() {
    // Arrange
    DailyReport domain = new DailyReport(1L, TODAY, 500.0, 5, 200.0, 150.0, 150.0, 300.0, 200.0, NOW);

    // Act
    DailyReportJpaEntity entity = DailyReportJpaEntity.fromDomain(domain);

    // Assert
    assertEquals(1L, entity.id);
    assertEquals(TODAY, entity.reportDate);
    assertEquals(500.0, entity.totalSales);
    assertEquals(5, entity.totalTransactions);
  }
}
