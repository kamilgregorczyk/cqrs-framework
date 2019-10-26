package com.kgregorczyk.store.cqrs.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.CaseFormat;
import com.kgregorczyk.store.cqrs.aggregate.Aggregate;
import com.kgregorczyk.store.cqrs.aggregate.Id;
import java.time.Instant;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class DomainEvent<A extends Aggregate> {

  @JsonProperty("id")
  public abstract Id<A> id();

  @JsonProperty("createdAt")
  public abstract Instant createdAt();

  @JsonIgnore
  public abstract Optional<Boolean> pendingEvent();

  @JsonIgnore
  public boolean isPendingEvent() {
    return pendingEvent().orElse(false);
  }

  /**
   * Converts for e.g. AutoValue__AccountCreatedEvent -> ACCOUNT_CREATED_EVENT
   */
  @JsonProperty("eventType")
  public String eventType() {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, this.getClass().getSimpleName())
        .split("AUTO_VALUE__")[1];
  }

  public abstract static class Builder<B extends Builder, A extends Aggregate> {

    @JsonProperty("id")
    public abstract B id(Id<A> value);

    @JsonProperty("createdAt")
    public abstract B createdAt(Instant value);

    @JsonIgnore
    public abstract B pendingEvent(Boolean value);
  }
}
