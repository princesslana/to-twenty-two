package com.github.princesslana.totwentytwo;

import java.util.Optional;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.princesslana.smalld.SmallD;

public class ToTwentyTwo implements Consumer<SmallD> {

  private Round round = new Round();

  public void accept(SmallD smalld) {
    smalld.onGatewayPayload(p -> {
      GatewayPayload gp = GatewayPayload.read(p);

      if (gp.op() == 0 && gp.t().equals(Optional.of("MESSAGE_CREATE"))) {
        Message msg = Message.read(gp.d());

        if (msg.getChannelId().equals(Config.getCountChannelId())) {
          round.onMessage(msg.getAuthor(), msg.getContent());

          if (round.isDone()) {
            send(smalld, round.getResult().format());

            round = new Round();
          }
        }
      }
    });
  }

  private static void send(SmallD smalld, String msg) {
    JsonNode response = Config.getJackson().createObjectNode().put("content", msg);

    try {
      smalld.post(
        "/channels/" + Config.getCountChannelId() + "/messages",
        Config.getJackson().writeValueAsString(response));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    SmallD.run(Config.getToken(), new ToTwentyTwo());
  }
}
