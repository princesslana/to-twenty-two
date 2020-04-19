package com.github.princesslana.totwentytwo;

import org.immutables.value.Value;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableUser.class)
public interface User {
  String getId();
  String getUsername();
  String getDiscriminator();

  default String getTag() {
    return getUsername() + "#" + getDiscriminator();
  }
}

