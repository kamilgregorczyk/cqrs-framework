package com.kgregorczyk.store.cqrs.kafka;

import com.kgregorczyk.store.cqrs.aggregate.Aggregate;
import com.kgregorczyk.store.cqrs.aggregate.Id;
import com.kgregorczyk.store.cqrs.event.DomainEventPublisher;
import com.kgregorczyk.store.cqrs.event.DomainEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class KafkaDomainEventRepository<A extends Aggregate<A, ?>>
    implements DomainEventRepository<A> {

  private final DomainEventPublisher<A> domainEventPublisher;
  private final ReadOnlyKeyValueStore<UUID, A> readOnlyKeyValueStore;

  @Autowired
  public KafkaDomainEventRepository(
      DomainEventPublisher<A> domainEventPublisher,
      ReadOnlyKeyValueStore<UUID, A> readOnlyKeyValueStore) {
    this.domainEventPublisher = domainEventPublisher;
    this.readOnlyKeyValueStore = readOnlyKeyValueStore;
  }

  @Override
  public A save(A aggregate) {
    if (aggregate.getId() != null) {
      log.info(
          "Saving {} events for [{}] of id [{}]",
          aggregate.getPendingEvents().stream()
              .map(event -> event.getClass().getSimpleName())
              .collect(Collectors.toList()),
          aggregate.getClass().getSimpleName(),
          aggregate.getId().getUuid());
    } else {
      log.info(
          "Saving {} events for new [{}]",
          aggregate.getPendingEvents().stream()
              .map(event -> event.getClass().getSimpleName())
              .collect(Collectors.toList()),
          aggregate.getClass().getSimpleName());
    }
    domainEventPublisher.publish(aggregate.getEventTopic(), aggregate.getPendingEvents());
    aggregate.flushEvents();
    return aggregate;
  }

  @Override
  public Optional<A> load(Id<A> id) {
    log.info("Retrieving aggregate of id [{}]", id.getUuid());
    return Optional.ofNullable(readOnlyKeyValueStore.get(id.getUuid()));
  }
}
