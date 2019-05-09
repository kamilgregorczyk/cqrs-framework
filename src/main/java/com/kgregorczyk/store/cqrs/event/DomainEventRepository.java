package com.kgregorczyk.store.cqrs.event;

import com.kgregorczyk.store.cqrs.aggregate.Aggregate;
import com.kgregorczyk.store.cqrs.aggregate.Id;

import java.util.Optional;

public interface DomainEventRepository<A extends Aggregate> {

  A save(A aggregate);

  Optional<A> load(Id<A> id);
}
