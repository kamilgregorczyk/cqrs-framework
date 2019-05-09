package com.kgregorczyk.store.product.controller;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class CreateProductDTO {
  @NotBlank private String name;
  @NotNull @Positive private BigDecimal price;
}
