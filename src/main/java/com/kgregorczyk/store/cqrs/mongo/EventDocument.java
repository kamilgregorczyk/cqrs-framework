package com.kgregorczyk.store.cqrs.mongo;

import com.mongodb.BasicDBObject;
import java.time.LocalDate;
import org.bson.conversions.Bson;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class EventDocument {

  @Id private String id;

  @Indexed private String aggregateType;

  @Indexed private String aggregateId;

  private String correlationId;

  private String eventType;

  private BasicDBObject eventData;

  private LocalDate createdAt = LocalDate.now();

  public EventDocument() {}

  public String getId() {
    return id;
  }

  public String getAggregateType() {
    return aggregateType;
  }

  public String getAggregateId() {
    return aggregateId;
  }

  public String getCorrelationId() {
    return correlationId;
  }

  public Bson getEventData() {
    return eventData;
  }

  public String getEventType() {
    return eventType;
  }

  public LocalDate getCreatedAt() {
    return createdAt;
  }

  public EventDocument setId(String id) {
    this.id = id;
    return this;
  }

  public EventDocument setAggregateType(String aggregateType) {
    this.aggregateType = aggregateType;
    return this;
  }

  public EventDocument setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
    return this;
  }

  public EventDocument setAggregateId(String aggregateId) {
    this.aggregateId = aggregateId;
    return this;
  }

  public EventDocument setEventData(BasicDBObject eventData) {
    this.eventData = eventData;
    return this;
  }

  public EventDocument setEventType(String eventType) {
    this.eventType = eventType;
    return this;
  }
}
