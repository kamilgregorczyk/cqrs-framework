package com.kgregorczyk.store.product.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import java.math.BigDecimal;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@AutoValue
public abstract class CreateProductDTO {

  @NotNull
  @Positive
  public abstract BigDecimal price();

  @NotNull
  public abstract String name();

  @JsonCreator
  public static CreateProductDTO aCreateProductDTO(@JsonProperty("price") BigDecimal price, @JsonProperty("name") String name){
    return new AutoValue_CreateProductDTO(price, name);
  }
}