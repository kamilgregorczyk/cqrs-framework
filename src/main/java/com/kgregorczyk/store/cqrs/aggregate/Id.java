package com.kgregorczyk.store.cqrs.aggregate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static java.util.UUID.randomUUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Id<A extends Aggregate> {
  private Class<A> type;
  private UUID uuid;

  public static <V extends Aggregate> Id<V> id(Class<V> type, UUID uuid) {
    return new Id<>(type, uuid);
  }

  public static <V extends Aggregate> Id<V> random(Class<V> type) {
    return new Id<>(type, randomUUID());
  }
}
