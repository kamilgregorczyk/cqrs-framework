package com.kgregorczyk.store.product.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.kgregorczyk.store.cqrs.command.DomainCommand;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import java.time.Instant;
import java.util.UUID;

@AutoValue
@JsonDeserialize(builder = AutoValue_UpdateProductNameCommand.Builder.class)
public abstract class UpdateProductNameCommand extends DomainCommand<ProductAggregate> {

  public static Builder anUpdateProductNameCommand() {
    return new AutoValue_UpdateProductNameCommand.Builder()
        .createdAt(Instant.now())
        .correlationId(UUID.randomUUID());
  }

  @JsonProperty("name")
  public abstract String name();

  public abstract Builder toBuilder();

  @AutoValue.Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonPOJOBuilder(withPrefix = "")
  public abstract static class Builder extends DomainCommand.Builder<Builder, ProductAggregate> {

    public abstract Builder name(String value);

    public abstract UpdateProductNameCommand build();
  }
}
