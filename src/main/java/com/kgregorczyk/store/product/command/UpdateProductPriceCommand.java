package com.kgregorczyk.store.product.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.kgregorczyk.store.cqrs.command.DomainCommand;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import java.math.BigDecimal;
import java.time.Instant;

@AutoValue
@JsonDeserialize(builder = AutoValue_UpdateProductPriceCommand.Builder.class)
public abstract class UpdateProductPriceCommand extends DomainCommand<ProductAggregate> {

  public static Builder anUpdateProductPriceCommand() {
    return new AutoValue_UpdateProductPriceCommand.Builder().createdAt(Instant.now());
  }

  @JsonProperty("price")
  public abstract BigDecimal price();

  public abstract Builder toBuilder();

  @AutoValue.Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonPOJOBuilder(withPrefix = "")
  public abstract static class Builder extends DomainCommand.Builder<Builder, ProductAggregate> {

    public abstract Builder price(BigDecimal value);

    public abstract UpdateProductPriceCommand build();
  }
}