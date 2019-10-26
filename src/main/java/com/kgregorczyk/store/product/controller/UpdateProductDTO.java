package com.kgregorczyk.store.product.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import java.math.BigDecimal;
import java.util.Optional;
import javax.validation.constraints.NotNull;

@AutoValue
public abstract class UpdateProductDTO {

  @NotNull
  public abstract Optional<BigDecimal> price();

  @NotNull
  public abstract Optional<String> name();

  @JsonCreator
  public static UpdateProductDTO anUpdateProductDto(
      @JsonProperty("price") Optional<BigDecimal> price,
      @JsonProperty("name") Optional<String> name) {
    return new AutoValue_UpdateProductDTO(price, name);
  }
}
