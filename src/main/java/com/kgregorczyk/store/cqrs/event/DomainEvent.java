package com.kgregorczyk.store.cqrs.event;

import com.kgregorczyk.store.cqrs.aggregate.Aggregate;
import com.kgregorczyk.store.cqrs.aggregate.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class DomainEvent<A extends Aggregate> {
  private Id<A> id;
  private UUID eventId;
  private Instant createdAt;
}
