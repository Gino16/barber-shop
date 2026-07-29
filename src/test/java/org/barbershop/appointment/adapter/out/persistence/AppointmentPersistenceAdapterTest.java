package org.barbershop.appointment.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.barbershop.appointment.domain.Appointment;
import org.barbershop.appointment.domain.AppointmentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentPersistenceAdapter Tests")
class AppointmentPersistenceAdapterTest {

  @Mock
  private AppointmentPanacheRepository repository;

  @InjectMocks
  private AppointmentPersistenceAdapter adapter;

  private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);

  private AppointmentJpaEntity sampleEntity(Long id) {
    AppointmentJpaEntity entity = new AppointmentJpaEntity();
    entity.id = id;
    entity.customerId = 1L;
    entity.employeeId = 2L;
    entity.scheduledAt = LocalDateTime.now();
    entity.notes = "Nota";
    entity.status = AppointmentStatus.SCHEDULED;
    entity.createdAt = LocalDateTime.now();
    return entity;
  }

  @Test
  @DisplayName("ReturnAppointment_WhenFindByIdCalledWithExistentId")
  @SuppressWarnings("unchecked")
  void shouldReturnAppointmentWhenFindByIdCalledWithExistentId() {
    // Arrange
    AppointmentJpaEntity entity = sampleEntity(1L);
    PanacheQuery<AppointmentJpaEntity> mockQuery = mock(PanacheQuery.class);
    when(repository.find("id", 1L)).thenReturn(mockQuery);
    when(mockQuery.firstResultOptional()).thenReturn(Optional.of(entity));

    // Act
    Optional<Appointment> result = adapter.findById(1L);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(1L, result.get().id());
    assertEquals(AppointmentStatus.SCHEDULED, result.get().status());
  }

  @Test
  @DisplayName("ReturnEmpty_WhenFindByIdCalledWithNonExistentId")
  @SuppressWarnings("unchecked")
  void shouldReturnEmptyWhenFindByIdCalledWithNonExistentId() {
    // Arrange
    PanacheQuery<AppointmentJpaEntity> mockQuery = mock(PanacheQuery.class);
    when(repository.find("id", 999L)).thenReturn(mockQuery);
    when(mockQuery.firstResultOptional()).thenReturn(Optional.empty());

    // Act
    Optional<Appointment> result = adapter.findById(999L);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("ConvertJpaEntityToDomain_WhenToDomainCalled")
  void shouldConvertJpaEntityToDomainWhenToDomainCalled() {
    // Arrange
    AppointmentJpaEntity entity = sampleEntity(1L);

    // Act
    Appointment domain = entity.toDomain();

    // Assert
    assertEquals(1L, domain.id());
    assertEquals(1L, domain.customerId());
    assertEquals(2L, domain.employeeId());
    assertEquals(AppointmentStatus.SCHEDULED, domain.status());
    assertEquals("Nota", domain.notes());
  }

  @Test
  @DisplayName("ConvertDomainToJpaEntity_WhenFromDomainCalled")
  void shouldConvertDomainToJpaEntityWhenFromDomainCalled() {
    // Arrange
    Appointment domain = new Appointment(1L, 1L, 2L, NOW, "Nota", AppointmentStatus.SCHEDULED, NOW);

    // Act
    AppointmentJpaEntity entity = AppointmentJpaEntity.fromDomain(domain);

    // Assert
    assertEquals(1L, entity.id);
    assertEquals(1L, entity.customerId);
    assertEquals(2L, entity.employeeId);
    assertEquals(AppointmentStatus.SCHEDULED, entity.status);
    assertEquals("Nota", entity.notes);
  }

  @Test
  @DisplayName("CallDeleteById_WhenDeleteCalled")
  void shouldCallDeleteByIdWhenDeleteCalled() {
    // Arrange
    when(repository.deleteById(1L)).thenReturn(true);

    // Act
    adapter.delete(1L);

    // Assert
    verify(repository).deleteById(1L);
  }

  @Test
  @DisplayName("PersistEntity_WhenSaveCalledWithNewAppointment")
  @SuppressWarnings("unchecked")
  void shouldPersistEntityWhenSaveCalledWithNewAppointment() {
    // Arrange
    Appointment newAppointment = new Appointment(null, 1L, 2L, NOW, "Nota", AppointmentStatus.SCHEDULED, NOW);
    // Panache void methods are no-ops by default in mocks — no stubbing needed

    // Act
    Appointment result = adapter.save(newAppointment);

    // Assert
    assertNotNull(result);
    assertEquals(AppointmentStatus.SCHEDULED, result.status());
  }
}
