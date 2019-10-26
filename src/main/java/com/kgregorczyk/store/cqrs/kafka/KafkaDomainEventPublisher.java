package com.kgregorczyk.store.cqrs.kafka;

import com.kgregorczyk.store.cqrs.aggregate.Aggregate;
import com.kgregorczyk.store.cqrs.event.DomainEvent;
import com.kgregorczyk.store.cqrs.event.DomainEventPublisher;
import java.util.Collection;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaDomainEventPublisher<A extends Aggregate<A, ?>>
    implements DomainEventPublisher<A> {

  private final Logger log = LoggerFactory.getLogger(getClass().getName());
  private final KafkaTemplate<UUID, DomainEvent<A>> kafkaTemplate;

  public KafkaDomainEventPublisher(KafkaTemplate<UUID, DomainEvent<A>> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void publish(String topic, Collection<DomainEvent<A>> events) {
    events.forEach(event -> publish(topic, event));
  }

  @Override
  public void publish(String topic, DomainEvent<A> event) {
    log.debug("Publishing on [{}] event [{}]", topic, event);
    kafkaTemplate.send(topic, event.id().uuid(), event);
  }
}
