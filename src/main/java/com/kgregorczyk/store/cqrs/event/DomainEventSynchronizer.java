package com.kgregorczyk.store.cqrs.event;

import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope
public class DomainEventSynchronizer {
  private static final Map<UUID, Object> CORRELATION_ID_TO_QUEUE = new ConcurrentHashMap<>();

  private final Logger log = LoggerFactory.getLogger(getClass().getName());

  public void record(UUID correlationId) {
    getOrCreateWaitObject(correlationId);
  }

  public void waitOneSecondFor(UUID correlationId) {
    waitFor(correlationId, 1);
  }

  public void waitFor(UUID correlationId, int seconds) {
    final var waitObject = getOrCreateWaitObject(correlationId);
    synchronized (waitObject) {
      try {
        waitObject.wait(TimeUnit.SECONDS.toMillis(seconds));
      } catch (InterruptedException e) {
        log.error("Failed to wait for event correlationId=[" + correlationId + "]", e);
        throw new UncheckedExecutionException(e);
      } finally {
        CORRELATION_ID_TO_QUEUE.remove(correlationId);
      }
    }
  }

  public void notify(UUID correlationId) {
    CORRELATION_ID_TO_QUEUE.computeIfPresent(
        correlationId,
        (id, obj) -> {
          synchronized (obj) {
            obj.notifyAll();
          }
          return obj;
        });
  }

  private Object getOrCreateWaitObject(UUID correlationId) {
    return CORRELATION_ID_TO_QUEUE.computeIfAbsent(correlationId, (id) -> new Object());
  }
}
