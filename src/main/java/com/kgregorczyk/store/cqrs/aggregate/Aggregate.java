package com.kgregorczyk.store.cqrs.aggregate;

import com.kgregorczyk.store.cqrs.event.DomainEvent;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public abstract class Aggregate<A extends Aggregate<A, S>, S extends GenericState> {
  private Id<A> id;
  private S state;
  private Instant createdAt;
  private Instant updatedAt;
  private List<DomainEvent<A>> pendingEvents = new ArrayList<>();

  public List<DomainEvent<A>> getPendingEvents() {
    return List.copyOf(pendingEvents);
  }

  public void flushEvents() {
    pendingEvents.clear();
  }

  public A addPendingEvent(DomainEvent<A> event) {
    pendingEvents.add(event);
    return (A) this;
  }

  protected void setUpdatedAt(Instant instant) {
    updatedAt = instant;
  }

  protected void setCreatedAt(Instant instant) {
    createdAt = instant;
  }

  public abstract String getEventTopic();
}
