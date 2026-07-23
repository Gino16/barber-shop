# 🧪 Unit Tests Documentation

## Overview

Comprehensive unit test suite siguiendo **Quarkus standards**, **JUnit 5**, **Mockito**, con patrón **AAA** (Arrange-Act-Assert), principios **KISS** y **YAGNI**.

**Cobertura: 22 tests unitarios ligeros sin mini-servers**

---

## 📋 Test Suites

### 1. ItemFilterQueryTest (6 tests)
**Archivo:** `src/test/java/org/barbershop/item/application/ItemFilterQueryTest.java`

Valida la lógica de filtrado y paginación:

```java
✅ ReturnDefaultValues_WhenWithDefaultsCalled
✅ ReturnValidValues_WhenWithDefaultsCalledWithValidData
✅ ReturnMaxPageSize_WhenPageSizeExceedsMax
✅ ReturnCorrectOffset_WhenOffsetCalled
✅ ReturnAscAsDefault_WhenSortDirectionIsInvalid
✅ TrimSearchString_WhenSearchHasWhitespace
```

**Principios:**
- Prueba validaciones de defaults (page=1, pageSize=10)
- Valida límites (máx pageSize=100)
- Cálculo correcto de offset para paginación

---

### 2. ItemServiceTest (7 tests)
**Archivo:** `src/test/java/org/barbershop/item/application/ItemServiceTest.java`

Tests del caso de uso con **Mockito**:

```java
✅ ReturnPaginatedItems_WhenListCalled
✅ ReturnItem_WhenFindByIdCalled
✅ ReturnEmptyOptional_WhenFindByIdCalledWithNonExistentId
✅ ReturnCreatedItem_WhenCreateCalled
✅ ReturnUpdatedItem_WhenUpdateCalledWithExistentId
✅ ReturnEmptyOptional_WhenUpdateCalledWithNonExistentId
✅ ReturnActiveItem_WhenCreateCalledWithoutActiveParam
```

**Patrón Usado:**
```java
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
  @Mock ItemRepositoryPort repositoryPort;
  @InjectMocks ItemService itemService;
  
  void test() {
    // Arrange
    when(repositoryPort.find(...)).thenReturn(...);
    
    // Act
    PagedResponse<Item> result = itemService.list(query);
    
    // Assert
    assertEquals(...);
    verify(repositoryPort).find(...);
  }
}
```

---

### 3. ItemPersistenceAdapterTest (4 tests)
**Archivo:** `src/test/java/org/barbershop/item/adapter/out/persistence/ItemPersistenceAdapterTest.java`

Tests de conversión JPA <-> Domain:

```java
✅ ReturnAllItems_WhenFindAllCalled
✅ ReturnEmptyList_WhenFindAllCalledOnEmptyDB
✅ ConvertJpaEntityToDomain_WhenToDomainCalled
✅ ConvertDomainToJpaEntity_WhenFromDomainCalled
```

**Propósito:**
- Verifica mapeo de entidades
- No mocka Panache (es ligero)
- Valida conversiones bidireccionales

---

### 4. PagedResponseTest (5 tests)
**Archivo:** `src/test/java/org/barbershop/item/application/PagedResponseTest.java`

Tests del DTO de respuesta paginada:

```java
✅ ReturnCorrectMetadata_WhenPagedResponseCreated
✅ ReturnNoNextPage_WhenOnLastPage
✅ ReturnOnePage_WhenTotalLessThanPageSize
✅ ReturnCorrectTotalPages_WhenExactMultiple
✅ ReturnHasNextPage_WhenNotOnLastPage
```

**Pruebas:**
- Cálculo correcto de `totalPages`
- Flag `hasNextPage` correcto
- Casos límite (última página, solo una página)

---

## 🏆 Principios Aplicados

### ✅ Naming Convention: Return...When
Todos los tests siguen el patrón:
```java
ReturnXXX_WhenYYY
```

Ejemplo:
```java
ReturnPaginatedItems_WhenListCalled
ReturnEmptyOptional_WhenFindByIdCalledWithNonExistentId
ReturnMaxPageSize_WhenPageSizeExceedsMax
```

### ✅ JUnit 5 + Mockito
```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

### ✅ Patrón AAA
```java
void testSomething() {
    // Arrange: Setup datos y mocks
    when(mock.method()).thenReturn(value);
    
    // Act: Ejecutar lógica
    Result result = service.doSomething();
    
    // Assert: Validar resultado
    assertEquals(expected, result);
    verify(mock).method();
}
```

### ✅ KISS & YAGNI
- Solo tests necesarios y claros
- Sin complejidad innecesaria
- Sin helper methods redundantes
- Tests unitarios puros (sin servidor)

---

## 📊 Resultados

```
[INFO] Running ItemPersistenceAdapter Tests
[INFO] Tests run: 4, Failures: 0, Errors: 0 ✅

[INFO] Running ItemFilterQuery Tests
[INFO] Tests run: 6, Failures: 0, Errors: 0 ✅

[INFO] Running ItemService Tests
[INFO] Tests run: 7, Failures: 0, Errors: 0 ✅

[INFO] Running PagedResponse Tests
[INFO] Tests run: 5, Failures: 0, Errors: 0 ✅

TOTAL: 22 tests ✅ 0 failures ✅
```

---

## 🚀 Ejecutar Tests

```bash
# Todos los tests
mvn test

# Solo tests unitarios (excluyendo integración)
mvn test -Dtest=ItemService*,ItemFilter*,PagedResponse*

# Test específico
mvn test -Dtest=ItemServiceTest#shouldReturnCreatedItemWhenCreateCalled
```

---

## 💡 Próximos Pasos

- [ ] REST Adapter tests (sin @QuarkusTest para ser ligeros)
- [ ] Validación de entrada en ItemCommand
- [ ] Error handling y custom exceptions
- [ ] Integration tests (separado de unit tests)

---

## 📝 Notas

- **Sin mini-servers levantados** ✅
- **Mockito con @Mock y @InjectMocks** ✅
- **JUnit 5 con @ExtendWith(MockitoExtension.class)** ✅
- **Patrón AAA claramente definido** ✅
- **Naming convention consistente** ✅
- **Principios KISS y YAGNI** ✅
