package com.kgregorczyk.store.cqrs.event;

import static io.vavr.API.unchecked;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.kgregorczyk.store.cqrs.aggregate.Aggregate;
import com.kgregorczyk.store.cqrs.aggregate.Id;
import com.kgregorczyk.store.cqrs.mongo.EventDocument;
import com.kgregorczyk.store.cqrs.mongo.EventDocumentRepository;
import com.mongodb.BasicDBObject;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MongoDomainEventRepository<A extends Aggregate<A, ?>>
    implements DomainEventRepository<A> {

  private final Logger log = LoggerFactory.getLogger(getClass().getName());

  private final DomainEventPublisher<A> domainEventPublisher;
  private final EventDocumentRepository eventDocumentRepository;
  private final ObjectMapper objectMapper;

  @Autowired
  public MongoDomainEventRepository(
      DomainEventPublisher<A> domainEventPublisher,
      EventDocumentRepository eventDocumentRepository, ObjectMapper objectMapper) {
    this.domainEventPublisher = domainEventPublisher;
    this.eventDocumentRepository = eventDocumentRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  @Transactional
  public A save(A aggregate) {
    if (!aggregate.hasChanged()) {
      log.info("No change for aggregate=[{}]", aggregate.id());
      return aggregate;
    }
    aggregate
        .getPendingEvents()
        .forEach(
            event -> {
              final var mappedEvent =
                  new EventDocument()
                      .setAggregateId(aggregate.id().uuid().toString())
                      .setCorrelationId(event.correlationId().toString())
                      .setAggregateType(aggregate.id().type().getSimpleName())
                      .setEventType(event.getClass().getTypeName())
                      .setEventData(objectMapper.convertValue(event, BasicDBObject.class));
              var watch = Stopwatch.createStarted();
              eventDocumentRepository.save(mappedEvent);
              log.debug("Took [{} ms] to save event", watch.stop().elapsed(TimeUnit.MILLISECONDS));
            });
    log.info(
        "Persisted [{}] events for aggregate=[{}]",
        aggregate.getPendingEvents().size(),
        aggregate.id());
    domainEventPublisher.publish(aggregate.getEventTopic(), aggregate.getPendingEvents());
    aggregate.flushEvents();
    return aggregate;
  }

  @Override
  public Stream<DomainEvent<A>> find(Id<A> id) {
    return eventDocumentRepository
        .streamAllByAggregateIdOrderByCreatedAt(id.uuid().toString())
        .map(
            eventDocument ->
                (DomainEvent<A>)
                    objectMapper.convertValue(
                        eventDocument.getEventData(),
                        unchecked(() -> Class.forName(eventDocument.getEventType())).apply()));
  }
}
