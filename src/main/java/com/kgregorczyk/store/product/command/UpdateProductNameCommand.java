package com.kgregorczyk.store.product.command;

import com.kgregorczyk.store.cqrs.aggregate.Id;
import com.kgregorczyk.store.cqrs.command.DomainCommand;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import lombok.*;

import java.time.Instant;

@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString
public class UpdateProductNameCommand extends DomainCommand<ProductAggregate> {
  private String name;

  @Builder
  public UpdateProductNameCommand(Id<ProductAggregate> id, String name) {
    super(id, Instant.now());
    this.name = name;
  }
}
