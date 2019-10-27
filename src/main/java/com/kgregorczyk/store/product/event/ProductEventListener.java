package com.kgregorczyk.store.product.event;

import com.kgregorczyk.store.cqrs.event.DomainEvent;
import com.kgregorczyk.store.cqrs.event.DomainEventSynchronizer;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductEventListener {

  private final Logger log = LoggerFactory.getLogger(getClass().getName());
  private final DomainEventSynchronizer domainEventSynchronizer;

  @Autowired
  ProductEventListener(DomainEventSynchronizer domainEventSynchronizer) {
    this.domainEventSynchronizer = domainEventSynchronizer;
  }

  @KafkaListener(
      topics = ProductAggregate.EVENT_TOPIC,
      containerFactory = "domainEventContainerFactory")
  public void commandHandler(DomainEvent<ProductAggregate> event) {
    log.info("Received event=[{}]", event);
    domainEventSynchronizer.notify(event.correlationId());
  }
}
