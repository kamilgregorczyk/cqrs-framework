package com.kgregorczyk.store.cqrs.kafka;

import com.kgregorczyk.store.cqrs.aggregate.Aggregate;
import com.kgregorczyk.store.cqrs.event.DomainEvent;
import com.kgregorczyk.store.cqrs.event.DomainEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collection;
import java.util.UUID;

@Slf4j
public class KafkaDomainEventPublisher<A extends Aggregate<A, ?>>
    implements DomainEventPublisher<A> {
  private final KafkaTemplate<UUID, DomainEvent<A>> kafkaTemplate;

  public KafkaDomainEventPublisher(KafkaTemplate<UUID, DomainEvent<A>> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void publish(String topic, DomainEvent<A> event) {
    log.info("Publishing on [{}] event [{}]", topic, event);
    kafkaTemplate.send(topic, event.getId().getUuid(), event);
  }

  @Override
  public void publish(String topic, Collection<DomainEvent<A>> events) {
    events.forEach(event -> publish(topic, event));
  }
}
