package com.kgregorczyk.store.cqrs.aggregate;

import static java.util.UUID.randomUUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import java.util.UUID;

@AutoValue
@JsonDeserialize(builder = AutoValue_Id.Builder.class)
public abstract class Id<A extends Aggregate> {

  public static <V extends Aggregate> Id<V> from(Class<V> type, UUID uuid) {
    return new AutoValue_Id.Builder<V>().type(type).uuid(uuid).build();
  }

  public static <V extends Aggregate> Id<V> random(Class<V> type) {
    return from(type, randomUUID());
  }

  @JsonProperty("uuid")
  public abstract UUID uuid();

  @JsonProperty("type")
  public abstract Class<A> type();

  @AutoValue.Builder
  @JsonPOJOBuilder(withPrefix = "")
  public abstract static class Builder<A extends Aggregate> {

    public abstract Builder<A> uuid(UUID value);

    public abstract Builder<A> type(Class<A> value);

    public abstract Id<A> build();
  }
}
