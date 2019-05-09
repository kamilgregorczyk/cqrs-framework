package com.kgregorczyk.store.cqrs.aggregate;

import static java.util.Arrays.asList;

public interface State<S extends State<S>> {

  String name();

  default boolean in(S... states) {
    return asList(states).contains(this);
  }
}
