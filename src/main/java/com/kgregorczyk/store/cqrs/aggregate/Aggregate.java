package com.kgregorczyk.store.cqrs.aggregate;

import com.kgregorczyk.store.cqrs.event.DomainEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public abstract class Aggregate<A extends Aggregate<A, S>, S extends GenericState> {

  private Id<A> id;
  private S state;
  private Instant createdAt;
  private Instant updatedAt;
  private List<DomainEvent<A>> pendingEvents = new ArrayList<>();
  private List<DomainEvent<A>> persistedEvents = new ArrayList<>();
  private List<DomainEvent<A>> rejectedEvents = new ArrayList<>();

  public List<DomainEvent<A>> persistedEvents() {
    return List.copyOf(persistedEvents);
  }

  public List<DomainEvent<A>> rejectedEvents() {
    return List.copyOf(rejectedEvents);
  }

  public List<DomainEvent<A>> pendingEvents() {
    return List.copyOf(pendingEvents);
  }

  public boolean hasChanged() {
    return !pendingEvents.isEmpty();
  }

  public void flushEvents() {
    persistedEvents.addAll(pendingEvents);
    pendingEvents.clear();
    rejectedEvents.clear();
  }

  public abstract A applyEvent(DomainEvent<A> event);

  protected A recordEvent(DomainEvent<A> event) {
    if (event.isPendingEvent()) {
      pendingEvents.add(event);
    } else {
      persistedEvents.add(event);
    }
    updatedAt(event.createdAt());
    return (A) this;
  }

  protected A recordRejectedEvent(DomainEvent<A> event) {
    rejectedEvents.add(event);
    return (A) this;
  }

  protected Aggregate<A, S> state(S state) {
    this.state = state;
    return this;
  }

  protected Aggregate<A, S> updatedAt(Instant instant) {
    this.updatedAt = instant;
    return this;
  }

  protected Aggregate<A, S> createdAt(Instant instant) {
    this.createdAt = instant;
    return this;
  }

  protected Aggregate<A, S> id(Id<A> id) {
    this.id = id;
    return this;
  }

  public Id<A> id() {
    return id;
  }

  public S state() {
    return state;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant updatedAt() {
    return updatedAt;
  }

  public abstract String getEventTopic();
}
