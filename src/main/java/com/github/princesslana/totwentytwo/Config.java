package com.github.princesslana.totwentytwo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Config {

  private static final ObjectMapper jackson = createJackson();

  private Config() {}

  public static String getToken() {
    return getString("TTT_TOKEN");
  }

  public static Set<String> getCountChannelId() {
    return new HashSet<>(Arrays.asList(getString("TTT_CHANNEL_ID").split(",")));
  }

  public static File getHistoryFolder() {
    return new File(Optional.ofNullable(System.getenv("TTT_DATA")).orElse("data"));
  }

  public static ObjectMapper getJackson() {
    return jackson;
  }

  private static String getString(String key) {
    return Optional.ofNullable(System.getenv(key))
        .orElseThrow(() -> new IllegalStateException("No config for " + key));
  }

  private static ObjectMapper createJackson() {
    ObjectMapper jackson =
        new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .enable(SerializationFeature.INDENT_OUTPUT);

    jackson.registerModule(new Jdk8Module());

    return jackson;
  }
}
