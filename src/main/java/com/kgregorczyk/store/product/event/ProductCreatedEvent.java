package com.kgregorczyk.store.product.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.kgregorczyk.store.cqrs.event.DomainEvent;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import java.math.BigDecimal;
import java.time.Instant;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProductCreatedEvent.Builder.class)
public abstract class ProductCreatedEvent extends DomainEvent<ProductAggregate> {

  public static Builder aProductCreatedEvent(boolean isPending) {
    return new AutoValue_ProductCreatedEvent.Builder()
        .pendingEvent(isPending)
        .createdAt(Instant.now());
  }

  @JsonProperty("price")
  public abstract BigDecimal price();

  @JsonProperty("name")
  public abstract String name();

  public abstract Builder toBuilder();

  @AutoValue.Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonPOJOBuilder(withPrefix = "")
  public abstract static class Builder extends DomainEvent.Builder<Builder, ProductAggregate> {

    public abstract Builder price(BigDecimal value);

    public abstract Builder name(String value);

    public abstract ProductCreatedEvent build();
  }
}
