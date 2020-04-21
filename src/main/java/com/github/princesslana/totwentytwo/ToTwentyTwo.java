package com.github.princesslana.totwentytwo;

import java.util.Optional;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.princesslana.smalld.SmallD;
import com.github.princesslana.smalld.SmallDException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToTwentyTwo implements Consumer<SmallD> {

  private static final Logger LOG = LoggerFactory.getLogger(ToTwentyTwo.class);

  private History history = History.load();
  private Round round = new Round();

  public void accept(SmallD smalld) {
    smalld.onGatewayPayload(p -> {
      GatewayPayload gp = GatewayPayload.read(p);

      if (gp.op() == 0 && gp.t().equals(Optional.of("MESSAGE_CREATE"))) {
        Message msg = Message.read(gp.d());

        if (msg.getChannelId().equals(Config.getCountChannelId())) {
          round.onMessage(msg.getAuthor(), msg.getContent());

          if (round.isDone()) {
            Result result = round.getResult();
            history.add(result);

            send(smalld, result.format());
            send(smalld, history.leaderboard());

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
    } catch (SmallDException e) {
      LOG.warn("Exception when sending", e);
    }
  }

  public static void main(String[] args) {
    SmallD.run(Config.getToken(), new ToTwentyTwo());
  }
}
