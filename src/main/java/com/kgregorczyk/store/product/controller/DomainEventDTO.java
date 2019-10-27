package com.kgregorczyk.store.product.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.value.AutoValue;
import java.time.Instant;
import java.util.UUID;

@AutoValue
public abstract class DomainEventDTO {

  public static DomainEventDTO.Builder aDomainEventDTO() {
    return new AutoValue_DomainEventDTO.Builder();
  }

  @JsonProperty("id")
  public abstract UUID id();

  @JsonProperty("type")
  public abstract String type();

  @JsonProperty("data")
  public abstract JsonNode data();

  @JsonProperty("createdAt")
  public abstract Instant createdAt();

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract DomainEventDTO.Builder id(UUID id);

    public abstract DomainEventDTO.Builder type(String type);

    public abstract DomainEventDTO.Builder data(JsonNode data);

    public abstract DomainEventDTO.Builder createdAt(Instant date);

    public abstract DomainEventDTO build();
  }
}
