package com.kgregorczyk.store.cqrs.command;

import com.kgregorczyk.store.cqrs.aggregate.Aggregate;
import com.kgregorczyk.store.cqrs.aggregate.Id;
import lombok.*;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class DomainCommand<A extends Aggregate> {
  private Id<A> id;
  private Instant createdAt;
}
