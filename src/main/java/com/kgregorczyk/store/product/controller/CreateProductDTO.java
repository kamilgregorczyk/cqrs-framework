package com.kgregorczyk.store.product.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import java.math.BigDecimal;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import org.checkerframework.checker.index.qual.LessThan;
import org.hibernate.validator.constraints.Length;

@AutoValue
public abstract class CreateProductDTO {

  @JsonCreator
  public static CreateProductDTO aCreateProductDTO(
      @JsonProperty("price") BigDecimal price, @JsonProperty("name") String name) {
    return new AutoValue_CreateProductDTO(price, name);
  }

  @NotNull
  @Positive
  @Max(value = 1_000_000L)
  public abstract BigDecimal price();

  @NotNull
  @Length(min = 4, max = 255)
  public abstract String name();
}
