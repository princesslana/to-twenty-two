package com.github.princesslana.totwentytwo;

import org.immutables.value.Value;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableMessage.class)
public interface Message {
  String getId();
  User getAuthor();
  @JsonProperty("channel_id") String getChannelId();
  String getContent();

  static Message read(TreeNode n) {
    try {
      return Config.getJackson().treeToValue(n, Message.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
