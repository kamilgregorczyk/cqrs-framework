package com.kgregorczyk.store.cqrs.event;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DomainEventSynchronizer {
  private final Logger log = LoggerFactory.getLogger(getClass().getName());

  private final RedissonClient redisson;
  private final Map<UUID, RSemaphore> correlationIdToSemaphore = new ConcurrentHashMap<>();

  @Autowired
  DomainEventSynchronizer(RedissonClient redisson) {
    this.redisson = redisson;
  }

  public void record(UUID correlationId) {
    correlationIdToSemaphore.computeIfAbsent(
        correlationId, uuid -> redisson.getSemaphore(uuid.toString()));
  }

  public void waitFor(UUID correlationId) {
    final var semaphore = correlationIdToSemaphore.get(correlationId);
    if (semaphore != null) {
      try {
        semaphore.acquire();
      } catch (InterruptedException e) {
        log.error(String.format("Failed to wait for event correlationId=[%s]", correlationId), e);
      } finally {
        correlationIdToSemaphore.remove(correlationId);
      }
    }
  }

  public void notify(UUID correlationId) {
    correlationIdToSemaphore.computeIfPresent(
        correlationId,
        (uuid, rSemaphore) -> {
          rSemaphore.release();
          return rSemaphore;
        });
  }
}
