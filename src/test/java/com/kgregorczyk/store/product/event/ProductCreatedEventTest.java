package com.kgregorczyk.store.product.event;

import static com.kgregorczyk.store.product.event.ProductCreatedEvent.aProductCreatedEvent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kgregorczyk.store.cqrs.aggregate.Id;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import java.math.BigDecimal;
import org.junit.Test;

public class ProductCreatedEventTest {


  @Test
  public void test() throws JsonProcessingException {
    final var event = aProductCreatedEvent().name("asd").id(Id.random(ProductAggregate.class))
        .price(BigDecimal.ONE).build();

    var mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    System.out.println(event);
    System.out.println(mapper.writeValueAsString(event));

  }

}