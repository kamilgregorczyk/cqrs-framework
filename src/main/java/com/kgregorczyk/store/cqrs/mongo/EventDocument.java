package com.kgregorczyk.store.cqrs.mongo;

import com.mongodb.BasicDBObject;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class EventDocument {

  @Id private String id;

  @Indexed private String aggregateType;

  @Indexed private String aggregateId;

  @Indexed private String correlationId;

  private String eventType;

  private BasicDBObject eventData;

  private Instant createdAt;

  public EventDocument() {}

  public String getId() {
    return id;
  }

  public EventDocument setId(String id) {
    this.id = id;
    return this;
  }

  public String getAggregateType() {
    return aggregateType;
  }

  public EventDocument setAggregateType(String aggregateType) {
    this.aggregateType = aggregateType;
    return this;
  }

  public String getAggregateId() {
    return aggregateId;
  }

  public EventDocument setAggregateId(String aggregateId) {
    this.aggregateId = aggregateId;
    return this;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public EventDocument setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
    return this;
  }

  public String getEventType() {
    return eventType;
  }

  public EventDocument setEventType(String eventType) {
    this.eventType = eventType;
    return this;
  }

  public BasicDBObject getEventData() {
    return eventData;
  }

  public EventDocument setEventData(BasicDBObject eventData) {
    this.eventData = eventData;
    return this;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public EventDocument setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }
}
