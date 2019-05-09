package com.kgregorczyk.store.cqrs.kafka;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;

public class UUIDDeserializer implements Deserializer<UUID> {
  private String encoding = "UTF8";

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    String propertyName = isKey ? "key.deserializer.encoding" : "value.deserializer.encoding";
    Object encodingValue = configs.get(propertyName);
    if (encodingValue == null) encodingValue = configs.get("deserializer.encoding");
    if (encodingValue instanceof String) encoding = (String) encodingValue;
  }

  @Override
  public UUID deserialize(String topic, byte[] data) {
    try {
      if (data == null) return null;
      else return UUID.fromString(new String(data, encoding));
    } catch (IllegalArgumentException | UnsupportedEncodingException e) {
      throw new SerializationException(
          "Error when deserializing byte[] to string due to unsupported encoding " + encoding);
    }
  }

  @Override
  public void close() {
    // nothing to do
  }
}
