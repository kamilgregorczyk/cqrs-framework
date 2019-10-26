package com.kgregorczyk.store.product.controller;

import static com.kgregorczyk.store.product.command.CreateProductCommand.aCreateProductCommand;
import static com.kgregorczyk.store.product.command.UpdateProductNameCommand.anUpdateProductNameCommand;
import static com.kgregorczyk.store.product.command.UpdateProductPriceCommand.anUpdateProductPriceCommand;
import static com.kgregorczyk.store.product.controller.ProductDTO.aProductDTO;
import static io.vavr.collection.Stream.ofAll;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toUnmodifiableList;

import com.kgregorczyk.store.cqrs.aggregate.Id;
import com.kgregorczyk.store.cqrs.event.DomainEventBus;
import com.kgregorczyk.store.cqrs.event.DomainEventRepository;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import com.kgregorczyk.store.product.command.CreateProductCommandHandler;
import com.kgregorczyk.store.product.command.UpdateProductNameCommandHandler;
import com.kgregorczyk.store.product.command.UpdateProductPriceCommandHandler;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/posts")
public class ProductController {

  private final CreateProductCommandHandler createProductCommandHandler;
  private final UpdateProductNameCommandHandler updateProductNameCommandHandler;
  private final UpdateProductPriceCommandHandler updateProductPriceCommandHandler;
  private final DomainEventBus domainEventBus;
  private final DomainEventRepository<ProductAggregate> eventRepository;

  @Autowired
  public ProductController(
      CreateProductCommandHandler createProductCommandHandler,
      UpdateProductNameCommandHandler updateProductNameCommandHandler,
      UpdateProductPriceCommandHandler updateProductPriceCommandHandler,
      DomainEventBus domainEventBus,
      DomainEventRepository<ProductAggregate> domainEventRepository) {
    this.createProductCommandHandler = createProductCommandHandler;
    this.updateProductNameCommandHandler = updateProductNameCommandHandler;
    this.updateProductPriceCommandHandler = updateProductPriceCommandHandler;
    this.domainEventBus = domainEventBus;
    this.eventRepository = domainEventRepository;
  }

  @GetMapping("/{id}")
  public ProductDTO getProduct(@PathVariable("id") String id) {
    final var productId = Id.from(ProductAggregate.class, UUID.fromString(id));
    return fetchProduct(productId);
  }

  @PostMapping("")
  public ProductDTO createProduct(@RequestBody @Valid CreateProductDTO dto) {
    var createCommand =
        aCreateProductCommand()
            .id(Id.random(ProductAggregate.class))
            .name(dto.name())
            .price(dto.price())
            .build();
    createProductCommandHandler.handle(createCommand);
    domainEventBus.waitFor(createCommand.correlationId());
    return fetchProduct(createCommand.id());
  }

  @PutMapping("/{id}")
  public ProductDTO updateProduct(
      @PathVariable("id") UUID id, @RequestBody @Valid UpdateProductDTO dto) {
    UUID lastCorrelationId = null;
    if (dto.name().isPresent()) {
      final var command =
          anUpdateProductNameCommand()
              .id(Id.from(ProductAggregate.class, id))
              .name(dto.name().get())
              .build();
      updateProductNameCommandHandler.handle(command);
      lastCorrelationId = command.correlationId();
    }
    if (dto.price().isPresent()) {
      final var command =
          anUpdateProductPriceCommand()
              .id(Id.from(ProductAggregate.class, id))
              .price(dto.price().get())
              .build();
      updateProductPriceCommandHandler.handle(command);
      lastCorrelationId = command.correlationId();
    }
    ofNullable(lastCorrelationId).ifPresent(domainEventBus::waitFor);
    return fetchProduct(Id.from(ProductAggregate.class, id));
  }

  private ProductDTO fetchProduct(Id<ProductAggregate> id) {
    final var events = eventRepository.find(id).collect(toUnmodifiableList());
    final var product =
        ofAll(events).foldLeft(new ProductAggregate(), (ProductAggregate::applyEvent));
    return aProductDTO()
        .id(product.id().uuid())
        .name(product.name())
        .price(product.price())
        .state(product.state())
        .events(events)
        .createdAt(product.createdAt())
        .updatedAt(product.updatedAt())
        .build();
  }
}
