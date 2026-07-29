package org.barbershop.common.filter;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraceIdResponseFilter Tests")
class TraceIdResponseFilterTest {

  @Mock
  private ContainerResponseContext responseContext;

  private final TraceIdResponseFilter filter = new TraceIdResponseFilter();

  @Test
  @DisplayName("AddTraceIdHeader_WhenValidTraceIdPresent")
  void shouldAddTraceIdHeaderWhenValidTraceIdPresent() {
    // Arrange
    String validTraceId = "4bf92f3577b34da6a3ce929d0e0e4736";
    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
    when(responseContext.getHeaders()).thenReturn(headers);

    Span mockSpan = mock(Span.class);
    SpanContext mockSpanContext = mock(SpanContext.class);
    when(mockSpan.getSpanContext()).thenReturn(mockSpanContext);
    when(mockSpanContext.getTraceId()).thenReturn(validTraceId);

    try (MockedStatic<Span> spanStatic = mockStatic(Span.class)) {
      spanStatic.when(Span::current).thenReturn(mockSpan);

      // Act
      filter.addTraceIdHeader(responseContext);

      // Assert
      assertTrue(headers.containsKey("X-Trace-ID"));
      assertEquals(validTraceId, headers.getFirst("X-Trace-ID"));
    }
  }

  @Test
  @DisplayName("NotAddTraceIdHeader_WhenInvalidAllZeroTraceId")
  void shouldNotAddTraceIdHeaderWhenInvalidAllZeroTraceId() {
    // Arrange
    String invalidTraceId = "00000000000000000000000000000000";
    MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

    Span mockSpan = mock(Span.class);
    SpanContext mockSpanContext = mock(SpanContext.class);
    when(mockSpan.getSpanContext()).thenReturn(mockSpanContext);
    when(mockSpanContext.getTraceId()).thenReturn(invalidTraceId);

    try (MockedStatic<Span> spanStatic = mockStatic(Span.class)) {
      spanStatic.when(Span::current).thenReturn(mockSpan);

      // Act
      filter.addTraceIdHeader(responseContext);

      // Assert
      assertFalse(headers.containsKey("X-Trace-ID"));
      verify(responseContext, never()).getHeaders();
    }
  }

  @Test
  @DisplayName("NotAddTraceIdHeader_WhenBlankTraceId")
  void shouldNotAddTraceIdHeaderWhenBlankTraceId() {
    // Arrange
    String blankTraceId = "   ";

    Span mockSpan = mock(Span.class);
    SpanContext mockSpanContext = mock(SpanContext.class);
    when(mockSpan.getSpanContext()).thenReturn(mockSpanContext);
    when(mockSpanContext.getTraceId()).thenReturn(blankTraceId);

    try (MockedStatic<Span> spanStatic = mockStatic(Span.class)) {
      spanStatic.when(Span::current).thenReturn(mockSpan);

      // Act
      filter.addTraceIdHeader(responseContext);

      // Assert
      verify(responseContext, never()).getHeaders();
    }
  }

  @Test
  @DisplayName("NotAddTraceIdHeader_WhenNullTraceId")
  void shouldNotAddTraceIdHeaderWhenNullTraceId() {
    // Arrange
    Span mockSpan = mock(Span.class);
    SpanContext mockSpanContext = mock(SpanContext.class);
    when(mockSpan.getSpanContext()).thenReturn(mockSpanContext);
    when(mockSpanContext.getTraceId()).thenReturn(null);

    try (MockedStatic<Span> spanStatic = mockStatic(Span.class)) {
      spanStatic.when(Span::current).thenReturn(mockSpan);

      // Act
      filter.addTraceIdHeader(responseContext);

      // Assert
      verify(responseContext, never()).getHeaders();
    }
  }
}
