package com.github.princesslana.totwentytwo;

import java.util.Optional;
import org.immutables.value.Value;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableGatewayPayload.class)
public interface GatewayPayload {
  int op();
  JsonNode d();
  Optional<String> t();

  static GatewayPayload read(String p) {
    try {
      return Config.getJackson().readValue(p, GatewayPayload.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
