package org.barbershop.employee.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.barbershop.employee.domain.Employee;
import org.barbershop.employee.domain.EmployeeRole;
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
@DisplayName("EmployeePersistenceAdapter Tests")
class EmployeePersistenceAdapterTest {

  @Mock
  private EmployeePanacheRepository repository;

  @InjectMocks
  private EmployeePersistenceAdapter adapter;

  private static final OffsetDateTime NOW = OffsetDateTime.now(ZoneOffset.UTC);

  private EmployeeJpaEntity sampleEntity(Long id) {
    EmployeeJpaEntity entity = new EmployeeJpaEntity();
    entity.id = id;
    entity.name = "Pedro López";
    entity.role = EmployeeRole.BARBER;
    entity.phone = "555-9999";
    entity.email = "pedro@mail.com";
    entity.active = true;
    entity.createdAt = LocalDateTime.now();
    return entity;
  }

  @Test
  @DisplayName("ReturnEmployee_WhenFindByIdCalledWithExistentId")
  @SuppressWarnings("unchecked")
  void shouldReturnEmployeeWhenFindByIdCalledWithExistentId() {
    // Arrange
    EmployeeJpaEntity entity = sampleEntity(1L);
    PanacheQuery<EmployeeJpaEntity> mockQuery = mock(PanacheQuery.class);
    when(repository.find("id", 1L)).thenReturn(mockQuery);
    when(mockQuery.firstResultOptional()).thenReturn(Optional.of(entity));

    // Act
    Optional<Employee> result = adapter.findById(1L);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(1L, result.get().id());
    assertEquals(EmployeeRole.BARBER, result.get().role());
  }

  @Test
  @DisplayName("ReturnEmpty_WhenFindByIdCalledWithNonExistentId")
  @SuppressWarnings("unchecked")
  void shouldReturnEmptyWhenFindByIdCalledWithNonExistentId() {
    // Arrange
    PanacheQuery<EmployeeJpaEntity> mockQuery = mock(PanacheQuery.class);
    when(repository.find("id", 999L)).thenReturn(mockQuery);
    when(mockQuery.firstResultOptional()).thenReturn(Optional.empty());

    // Act
    Optional<Employee> result = adapter.findById(999L);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  @DisplayName("PersistEntity_WhenSaveCalledWithNewEmployee")
  void shouldPersistEntityWhenSaveCalledWithNewEmployee() {
    // Arrange
    Employee newEmployee = new Employee(null, "Pedro López", EmployeeRole.BARBER, "555-9999", "pedro@mail.com", true, NOW);
    // Panache void methods are no-ops by default in mocks — no stubbing needed

    // Act
    Employee result = adapter.save(newEmployee);

    // Assert
    assertNotNull(result);
    assertEquals("Pedro López", result.name());
    assertEquals(EmployeeRole.BARBER, result.role());
  }

  @Test
  @DisplayName("UpdateEntity_WhenSaveCalledWithExistingEmployee")
  @SuppressWarnings("unchecked")
  void shouldUpdateEntityWhenSaveCalledWithExistingEmployee() {
    // Arrange
    Employee existingEmployee = new Employee(1L, "Pedro Actualizado", EmployeeRole.MANAGER, "555-0000", "nuevo@mail.com", false, NOW);
    EmployeeJpaEntity existingEntity = sampleEntity(1L);
    PanacheQuery<EmployeeJpaEntity> mockQuery = mock(PanacheQuery.class);
    when(repository.find("id", 1L)).thenReturn(mockQuery);
    when(mockQuery.firstResult()).thenReturn(existingEntity);

    // Act
    Employee result = adapter.save(existingEmployee);

    // Assert
    assertNotNull(result);
    assertEquals("Pedro Actualizado", result.name());
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
  @DisplayName("ConvertJpaEntityToDomain_WhenToDomainCalled")
  void shouldConvertJpaEntityToDomainWhenToDomainCalled() {
    // Arrange
    EmployeeJpaEntity entity = sampleEntity(1L);

    // Act
    Employee domain = entity.toDomain();

    // Assert
    assertEquals(1L, domain.id());
    assertEquals("Pedro López", domain.name());
    assertEquals(EmployeeRole.BARBER, domain.role());
    assertTrue(domain.active());
  }

  @Test
  @DisplayName("ConvertDomainToJpaEntity_WhenFromDomainCalled")
  void shouldConvertDomainToJpaEntityWhenFromDomainCalled() {
    // Arrange
    Employee domain = new Employee(1L, "Pedro López", EmployeeRole.BARBER, "555-9999", "pedro@mail.com", true, NOW);

    // Act
    EmployeeJpaEntity entity = EmployeeJpaEntity.fromDomain(domain);

    // Assert
    assertEquals(1L, entity.id);
    assertEquals("Pedro López", entity.name);
    assertEquals(EmployeeRole.BARBER, entity.role);
    assertTrue(entity.active);
  }
}
