package com.kgregorczyk.store.product.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import java.math.BigDecimal;
import java.util.Optional;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

@AutoValue
public abstract class UpdateProductDTO {

  @JsonCreator
  public static UpdateProductDTO anUpdateProductDto(
      @JsonProperty("price") Optional<BigDecimal> price,
      @JsonProperty("name") Optional<String> name) {
    return new AutoValue_UpdateProductDTO(price, name);
  }

  @NotNull
  public abstract Optional<@Positive @Max(value = 1_000_000L) BigDecimal> price();

  @NotNull
  public abstract Optional<@Length(min = 4, max = 255) String> name();
}
