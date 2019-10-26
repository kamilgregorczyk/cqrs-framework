package com.kgregorczyk.store.cqrs.event;

import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope
public class DomainEventBus {
  private static final Map<UUID, BlockingQueue<DomainEvent>> CORRELATION_ID_TO_QUEUE =
      new ConcurrentHashMap<>();

  private final Logger log = LoggerFactory.getLogger(getClass().getName());

  public void record(UUID correlationId) {
    getOrCreateQueue(correlationId);
  }

  public DomainEvent waitFor(UUID correlationId) {
    synchronized (correlationId) {
      try {
        return getOrCreateQueue(correlationId).poll(3, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        log.error("Failed to wait for event correlationId=[" + correlationId + "]", e);
        throw new UncheckedExecutionException(e);
      } finally {
        CORRELATION_ID_TO_QUEUE.remove(correlationId);
      }
    }
  }

  public void notify(DomainEvent domainEvent) {
    CORRELATION_ID_TO_QUEUE.computeIfPresent(
        domainEvent.correlationId(),
        (id, queue) -> {
          queue.offer(domainEvent);
          return queue;
        });
  }

  private BlockingQueue<DomainEvent> getOrCreateQueue(UUID correlationId) {
    return CORRELATION_ID_TO_QUEUE.computeIfAbsent(
        correlationId, (id) -> new ArrayBlockingQueue<>(1));
  }
}
