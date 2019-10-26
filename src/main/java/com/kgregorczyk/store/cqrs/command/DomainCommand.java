package com.kgregorczyk.store.cqrs.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.CaseFormat;
import com.kgregorczyk.store.cqrs.aggregate.Aggregate;
import com.kgregorczyk.store.cqrs.aggregate.Id;
import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class DomainCommand<A extends Aggregate> {

  @JsonProperty("id")
  public abstract Id<A> id();

  @JsonProperty("createdAt")
  public abstract Instant createdAt();

  @JsonProperty("correlationId")
  public abstract UUID correlationId();

  /**
   * Converts for e.g. AutoValue__CreateProductCommand -> CREATE_PRODUCT_COMMAND
   */
  @JsonProperty("commandType")
  public String commandType() {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, this.getClass().getSimpleName())
        .split("AUTO_VALUE__")[1];
  }

  public abstract static class Builder<B extends Builder, A extends Aggregate> {

    @JsonProperty("id")
    public abstract B id(Id<A> value);

    @JsonProperty("createdAt")
    public abstract B createdAt(Instant value);

    @JsonProperty("correlationId")
    public abstract B correlationId(UUID value);
  }
}