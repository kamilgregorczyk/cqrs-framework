package com.kgregorczyk.store.cqrs.kafka;

import com.kgregorczyk.store.cqrs.aggregate.Aggregate;
import com.kgregorczyk.store.cqrs.command.DomainCommand;
import com.kgregorczyk.store.cqrs.command.DomainCommandPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collection;
import java.util.UUID;

@Slf4j
public class KafkaDomainCommandPublisher<A extends Aggregate<A, ?>>
    implements DomainCommandPublisher<A> {

  private final KafkaTemplate<UUID, DomainCommand<A>> kafkaTemplate;

  public KafkaDomainCommandPublisher(KafkaTemplate<UUID, DomainCommand<A>> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void publish(String topic, Collection<DomainCommand<A>> events) {
    events.forEach(event -> publish(topic, event));
  }

  @Override
  public void publish(String topic, DomainCommand<A> event) {
    log.info("Publishing on [{}] command [{}]", topic, event);
    kafkaTemplate.send(topic, event.getId().getUuid(), event);
  }
}
