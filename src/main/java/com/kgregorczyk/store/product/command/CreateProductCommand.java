package com.kgregorczyk.store.product.command;

import com.kgregorczyk.store.cqrs.aggregate.Id;
import com.kgregorczyk.store.cqrs.command.DomainCommand;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString
public class CreateProductCommand extends DomainCommand<ProductAggregate> {
  private String name;
  private BigDecimal price;

  @Builder
  public CreateProductCommand(Id<ProductAggregate> id, String name, BigDecimal price) {
    super(id, Instant.now());
    this.name = name;
    this.price = price;
  }
}
