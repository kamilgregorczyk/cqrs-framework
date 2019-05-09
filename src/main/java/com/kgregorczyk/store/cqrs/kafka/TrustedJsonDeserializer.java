package com.kgregorczyk.store.cqrs.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.kafka.support.serializer.JsonDeserializer;

public class TrustedJsonDeserializer<T> extends JsonDeserializer<T> {
  public TrustedJsonDeserializer() {
    super(createObjectMapper());

    // add our packages
    this.addTrustedPackages("*");
  }

  private static ObjectMapper createObjectMapper() {
    var mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }
}
