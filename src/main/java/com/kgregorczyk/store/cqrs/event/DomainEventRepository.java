package com.kgregorczyk.store.cqrs.event;

import com.kgregorczyk.store.cqrs.aggregate.Aggregate;
import com.kgregorczyk.store.cqrs.aggregate.Id;
import java.util.stream.Stream;

public interface DomainEventRepository<A extends Aggregate> {

  A save(A aggregate);

  Stream<DomainEvent<A>> find(Id<A> id);
}
