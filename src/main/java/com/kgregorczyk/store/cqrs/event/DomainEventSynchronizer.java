package com.kgregorczyk.store.cqrs.event;

import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DomainEventSynchronizer {
  private static final Map<UUID, Object> CORRELATION_ID_TO_LOCK = new ConcurrentHashMap<>();

  private final Logger log = LoggerFactory.getLogger(getClass().getName());
  private final RTopic topic;

  @Autowired
  DomainEventSynchronizer(RedissonClient redisson) {
    this.topic = redisson.getTopic("domain-events");
    topic.addListener(
        UUID.class,
        (channel, correlationId) -> {
          if (CORRELATION_ID_TO_LOCK.containsKey(correlationId)) {
            final var waitObject = CORRELATION_ID_TO_LOCK.get(correlationId);
            synchronized (waitObject) {
              waitObject.notifyAll();
            }
          }
        });
  }

  public void record(UUID correlationId) {
    CORRELATION_ID_TO_LOCK.computeIfAbsent(correlationId, (id) -> new Object());
  }

  public void waitOneSecondFor(UUID correlationId) {
    waitFor(correlationId, 1);
  }

  public void waitFor(UUID correlationId, int seconds) {
    CORRELATION_ID_TO_LOCK.computeIfPresent(
        correlationId,
        ((uuid, waitObject) -> {
          synchronized (waitObject) {
            try {
              waitObject.wait(TimeUnit.SECONDS.toMillis(seconds));
            } catch (InterruptedException e) {
              log.error("Failed to wait for event correlationId=[" + correlationId + "]", e);
              throw new UncheckedExecutionException(e);
            }
            return null;
          }
        }));
  }

  public void notify(UUID correlationId) {
    topic.publish(correlationId);
  }
}
