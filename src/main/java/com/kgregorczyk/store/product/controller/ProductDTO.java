package com.kgregorczyk.store.product.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.kgregorczyk.store.cqrs.aggregate.GenericState;
import com.kgregorczyk.store.cqrs.aggregate.Id;
import com.kgregorczyk.store.cqrs.event.DomainEvent;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AutoValue
public abstract class ProductDTO {

  @JsonProperty("id")
  public abstract UUID id();

  @JsonProperty("state")
  public abstract GenericState state();

  @JsonProperty("price")
  public abstract BigDecimal price();

  @JsonProperty("name")
  public abstract String name();

  @JsonProperty("createdAt")
  public abstract Instant createdAt();

  @JsonProperty("updatedAt")
  public abstract Instant updatedAt();

  @JsonProperty("events")
  public abstract List<DomainEvent<ProductAggregate>> events();

  public static ProductDTO.Builder aProductDTO() {
    return new AutoValue_ProductDTO.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract ProductDTO.Builder id(UUID id);

    public abstract ProductDTO.Builder state(GenericState state);

    public abstract ProductDTO.Builder price(BigDecimal bigDecimal);

    public abstract ProductDTO.Builder name(String name);

    public abstract ProductDTO.Builder createdAt(Instant date);

    public abstract ProductDTO.Builder updatedAt(Instant date);

    public abstract ProductDTO.Builder events(List<DomainEvent<ProductAggregate>> events);

    public abstract ProductDTO build();
  }
}
