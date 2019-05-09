package com.kgregorczyk.store.product.controller;

import com.kgregorczyk.store.cqrs.aggregate.Id;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import com.kgregorczyk.store.product.command.CreateProductCommand;
import com.kgregorczyk.store.product.command.CreateProductCommandHandler;
import com.kgregorczyk.store.product.command.UpdateProductNameCommand;
import com.kgregorczyk.store.product.command.UpdateProductNameCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ProductController {

  private final CreateProductCommandHandler createProductCommandHandler;
  private final UpdateProductNameCommandHandler updateProductNameCommandHandler;

  @Autowired
  public ProductController(
      CreateProductCommandHandler createProductCommandHandler,
      UpdateProductNameCommandHandler updateProductNameCommandHandler) {
    this.createProductCommandHandler = createProductCommandHandler;
    this.updateProductNameCommandHandler = updateProductNameCommandHandler;
  }

  @PostMapping()
  public void createProduct(@RequestBody @Valid CreateProductDTO dto) {
    var command =
        CreateProductCommand.builder()
            .id(Id.random(ProductAggregate.class))
            .name(dto.getName())
            .price(dto.getPrice())
            .build();
    createProductCommandHandler.handle(command);
    updateProductNameCommandHandler.handle(
        UpdateProductNameCommand.builder()
            .id(command.getId())
            .name(command.getName() + "aa $$")
            .build());
  }
}
