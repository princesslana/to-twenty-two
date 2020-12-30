package com.github.princesslana.totwentytwo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.princesslana.smalld.SmallD;
import com.github.princesslana.smalld.SmallDException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToTwentyTwo implements Consumer<SmallD> {

  private static final Logger LOG = LoggerFactory.getLogger(ToTwentyTwo.class);

  private Map<String, History> histories = new HashMap<>();
  private Map<String, Round> rounds = new HashMap<>();

  private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  public void accept(SmallD smalld) {
    executor.scheduleWithFixedDelay(
        () -> rounds.keySet().forEach(cid -> checkForDone(smalld, cid, true)),
        0,
        1,
        TimeUnit.MINUTES);

    smalld.onGatewayPayload(
        p -> {
          GatewayPayload gp = GatewayPayload.read(p);

          if (gp.op() == 0 && gp.t().equals(Optional.of("READY"))) {
            for (var channelId : Config.getCountChannelId()) {
              if (!rounds.containsKey(channelId)) {
                newRound(smalld, channelId);
              }
            }
          } else if (gp.op() == 0 && gp.t().equals(Optional.of("MESSAGE_CREATE"))) {
            Message msg = Message.read(gp.d());

            if (Config.getCountChannelId().contains(msg.getChannelId())) {
              if (!rounds.containsKey(msg.getChannelId())) {
                newRound(smalld, msg.getChannelId());
              }

              Round round = rounds.get(msg.getChannelId());

              round.onMessage(msg.getAuthor(), msg.getContent());

              checkForDone(smalld, msg.getChannelId(), false);
            }
          }
        });
  }

  private boolean isTraditional(String channelId) {
    return Config.getTraditionalCountChannelIds().contains(channelId);
  }

  private boolean isRapid(String channelId) {
    return Config.getRapidCountChannelIds().contains(channelId);
  }

  private void newRound(SmallD smalld, String channelId) {
    if (isTraditional(channelId)) {
      rounds.put(channelId, new Traditional());
    } else if (isRapid(channelId)) {
      rounds.put(channelId, new Rapid());
    } else {
      throw new IllegalStateException(
          "Asked to create a new round in unconfigured channel " + channelId);
    }
    send(smalld, channelId, "Ready for a new round to begin!");
  }

  private synchronized void checkForDone(SmallD smalld, String channelId, boolean checkTimeout) {
    Round round = rounds.get(channelId);

    if (round.isDone(checkTimeout)) {
      Result result = round.getResult();

      if (!histories.containsKey(channelId)) {
        histories.put(channelId, History.load(channelId));
      }
      History history = histories.get(channelId);

      history.add(result);

      send(smalld, channelId, result.format());
      send(smalld, channelId, history.leaderboard(isTraditional(channelId)));

      newRound(smalld, channelId);
    }
  }

  private static void send(SmallD smalld, String channelId, String msg) {
    JsonNode response = Config.getJackson().createObjectNode().put("content", msg);

    try {
      smalld.post(
          "/channels/" + channelId + "/messages", Config.getJackson().writeValueAsString(response));
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
