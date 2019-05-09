package com.kgregorczyk.store.product.event;

import com.kgregorczyk.store.cqrs.aggregate.Id;
import com.kgregorczyk.store.cqrs.event.DomainEvent;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ProductNameUpdatedEvent extends DomainEvent<ProductAggregate> {
  private final String name;

  @Builder
  public ProductNameUpdatedEvent(Id<ProductAggregate> id, String name) {
    super(checkNotNull(id), UUID.randomUUID(), Instant.now());
    this.name = checkNotNull(name);
  }
}
