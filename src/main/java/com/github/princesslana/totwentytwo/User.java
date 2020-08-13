package com.github.princesslana.totwentytwo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

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
