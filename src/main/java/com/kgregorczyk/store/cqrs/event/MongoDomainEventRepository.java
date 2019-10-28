package com.kgregorczyk.store.cqrs.event;

import static com.google.common.base.Stopwatch.createStarted;
import static io.vavr.API.unchecked;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgregorczyk.store.cqrs.aggregate.Aggregate;
import com.kgregorczyk.store.cqrs.aggregate.Id;
import com.kgregorczyk.store.cqrs.mongo.EventDocument;
import com.kgregorczyk.store.cqrs.mongo.EventDocumentRepository;
import com.mongodb.BasicDBObject;
import java.util.List;
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
  private final EventDocumentRepository eventRepository;
  private final DomainEventSynchronizer domainEventSynchronizer;
  private final ObjectMapper objectMapper;

  @Autowired
  public MongoDomainEventRepository(
      DomainEventPublisher<A> domainEventPublisher,
      EventDocumentRepository eventRepository,
      DomainEventSynchronizer domainEventSynchronizer,
      ObjectMapper objectMapper) {
    this.domainEventPublisher = domainEventPublisher;
    this.eventRepository = eventRepository;
    this.domainEventSynchronizer = domainEventSynchronizer;
    this.objectMapper = objectMapper;
  }

  @Override
  @Transactional
  public A save(A aggregate) {
    if (!aggregate.hasChanged()) {
      log.info("No change for aggregate=[{}]", aggregate.id());
      notifyOnRejectedEvents(aggregate.rejectedEvents());
    } else {
      persistEvents(aggregate.id(), aggregate.pendingEvents());
      domainEventPublisher.publish(aggregate.getEventTopic(), aggregate.pendingEvents());
    }
    aggregate.flushEvents();
    return aggregate;
  }

  @Override
  public Stream<DomainEvent<A>> find(Id<A> id) {
    return eventRepository
        .streamAllByAggregateIdOrderByCreatedAt(id.uuid().toString())
        .map(
            eventDocument ->
                (DomainEvent<A>)
                    objectMapper.convertValue(
                        eventDocument.getEventData(),
                        unchecked(() -> Class.forName(eventDocument.getEventType())).apply()));
  }

  private void notifyOnRejectedEvents(List<DomainEvent<A>> rejectedEvents) {
    rejectedEvents.forEach(event -> domainEventSynchronizer.notify(event.correlationId()));
  }

  private void persistEvents(Id<A> id, List<DomainEvent<A>> pendingEvents) {
    var watch = createStarted();
    pendingEvents.forEach(
        event -> {
          final var mappedEvent =
              new EventDocument()
                  .setAggregateId(id.uuid().toString())
                  .setCorrelationId(event.correlationId().toString())
                  .setAggregateType(id.type().getSimpleName())
                  .setEventType(event.getClass().getTypeName())
                  .setCreatedAt(event.createdAt())
                  .setEventData(objectMapper.convertValue(event, BasicDBObject.class));
          eventRepository.save(mappedEvent);
        });
    log.debug("Took [{} ms] to save events", watch.stop().elapsed(TimeUnit.MILLISECONDS));
    log.info("Persisted [{}] events for aggregate=[{}]", pendingEvents.size(), id);
  }
}
