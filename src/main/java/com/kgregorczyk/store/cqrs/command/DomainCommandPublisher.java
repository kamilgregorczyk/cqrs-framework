package com.kgregorczyk.store.cqrs.command;

import com.kgregorczyk.store.cqrs.aggregate.Aggregate;

import java.util.Collection;

public interface DomainCommandPublisher<A extends Aggregate<A, ?>> {

  void publish(String topic, Collection<DomainCommand<A>> events);

  void publish(String topic, DomainCommand<A> event);
}
