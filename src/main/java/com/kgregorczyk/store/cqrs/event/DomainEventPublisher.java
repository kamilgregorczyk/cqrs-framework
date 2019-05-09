package com.kgregorczyk.store.cqrs.event;

import com.kgregorczyk.store.cqrs.aggregate.Aggregate;

import java.util.Collection;

public interface DomainEventPublisher<A extends Aggregate<A, ?>> {

  void publish(String topic, DomainEvent<A> event);

  void publish(String topic, Collection<DomainEvent<A>> events);
}
