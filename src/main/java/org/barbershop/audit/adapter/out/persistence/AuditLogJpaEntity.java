package org.barbershop.audit.adapter.out.persistence;

import org.barbershop.audit.domain.AuditAction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnTransformer;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "audit_log")
public class AuditLogJpaEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(name = "entity_type", nullable = false)
  public String entityType;

  @Column(name = "entity_id", nullable = false)
  public Long entityId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  public AuditAction action;

  @ColumnTransformer(write = "?::jsonb")
  @Column(name = "old_values", columnDefinition = "jsonb")
  public String oldValuesJson;

  @ColumnTransformer(write = "?::jsonb")
  @Column(name = "new_values", columnDefinition = "jsonb")
  public String newValuesJson;

  @Column(name = "user_name", nullable = false, length = 100)
  public String userName;

  @Column(name = "timestamp", nullable = false)
  public LocalDateTime timestamp;

  @Transient
  private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

  public Map<String, Object> getOldValues() {
    if (oldValuesJson == null) return null;
    try {
      return mapper.readValue(oldValuesJson, Map.class);
    } catch (Exception e) {
      return null;
    }
  }

  public void setOldValues(Map<String, Object> values) {
    try {
      this.oldValuesJson = values == null ? null : mapper.writeValueAsString(values);
    } catch (Exception e) {
      this.oldValuesJson = null;
    }
  }

  public Map<String, Object> getNewValues() {
    if (newValuesJson == null) return null;
    try {
      return mapper.readValue(newValuesJson, Map.class);
    } catch (Exception e) {
      return null;
    }
  }

  public void setNewValues(Map<String, Object> values) {
    try {
      this.newValuesJson = values == null ? null : mapper.writeValueAsString(values);
    } catch (Exception e) {
      this.newValuesJson = null;
    }
  }
}
