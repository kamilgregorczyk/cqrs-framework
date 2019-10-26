package com.kgregorczyk.store.cqrs.kafka;

import com.kgregorczyk.store.cqrs.aggregate.Aggregate;
import com.kgregorczyk.store.cqrs.command.DomainCommand;
import com.kgregorczyk.store.cqrs.command.DomainCommandPublisher;
import java.util.Collection;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaDomainCommandPublisher<A extends Aggregate<A, ?>>
    implements DomainCommandPublisher<A> {

  private final Logger log = LoggerFactory.getLogger(getClass().getName());
  private final KafkaTemplate<UUID, DomainCommand<A>> kafkaTemplate;

  public KafkaDomainCommandPublisher(KafkaTemplate<UUID, DomainCommand<A>> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void publish(String topic, Collection<DomainCommand<A>> events) {
    kafkaTemplate.executeInTransaction(
        kt -> events.stream().map(event -> kt.send(topic, event.id().uuid(), event)));
  }

  @Override
  public void publish(String topic, DomainCommand<A> event) {
    log.debug("Publishing on [{}] command [{}]", topic, event);
    kafkaTemplate.send(topic, event.id().uuid(), event);
  }
}
