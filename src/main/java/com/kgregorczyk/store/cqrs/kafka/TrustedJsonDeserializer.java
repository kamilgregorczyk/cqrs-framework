package com.kgregorczyk.store.cqrs.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

@Component
public class TrustedJsonDeserializer<T> extends JsonDeserializer<T> {

  @Autowired
  public TrustedJsonDeserializer(ObjectMapper objectMapper) {
    super(objectMapper);

    // add our packages
    this.addTrustedPackages("*");
  }
}
