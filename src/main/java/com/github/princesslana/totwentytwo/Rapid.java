package com.github.princesslana.totwentytwo;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Rapid implements Round {

  private static final Logger LOG = LoggerFactory.getLogger(Rapid.class);

  private List<User> count = new ArrayList<>();

  private User loser;

  private Instant firstCount;

  public void onMessage(User who, String msg) {
    if (isDone() || (count.isEmpty() && !msg.equals("1"))) {
      return;
    }

    boolean isExpectedNumber = msg.equals(getExpectedCount().toString());
    boolean isSameAsLastUser = Optional.of(who).equals(getLastUser());

    if (isExpectedNumber && !isSameAsLastUser) {
      LOG.info("{} got {}", who.getTag(), getExpectedCount());
      if (count.isEmpty()) {
        firstCount = Instant.now();
      }
      count.add(who);
    } else {
      LOG.info("{} lost", who.getTag());
      loser = who;
    }
  }

  public boolean isDone() {
    boolean isTimeOut =
        firstCount != null
            && Duration.between(firstCount, Instant.now()).compareTo(Duration.ofMinutes(22)) > 0;

    return loser != null || count.size() == 22 || isTimeOut;
  }

  public Result getResult() {
    if (!isDone()) {
      throw new IllegalStateException("Can't get result of unfinished round");
    }

    ImmutableResult.Builder r = ImmutableResult.builder();

    Map<User, Long> scores = new HashMap<>();

    if (loser == null) {
      User winner = count.get(count.size() - 1);
      User assist = count.size() == 22 ? count.get(20) : null;
      r = r.winner(winner);

      getPlayers().stream().forEach(p -> scores.put(p, -getSum(p)));

      if (assist != null && getPlayers().size() > 2) {
        scores.put(assist, 0L);
      }

      scores.put(winner, getSum(winner));
    } else {
      r = r.loser(loser);

      getPlayers().stream().forEach(p -> scores.put(p, 0L));

      scores.put(loser, -getSum(loser));
    }

    scores.entrySet().stream()
        .map(e -> ImmutableScore.builder().user(e.getKey()).score(e.getValue()).build())
        .forEach(r::addScores);

    return r.build();
  }

  private Integer getExpectedCount() {
    return count.size() + 1;
  }

  private Set<User> getPlayers() {
    Set<User> ps = new HashSet<>(count);

    if (loser != null) {
      ps.add(loser);
    }

    return ps;
  }

  private long pointsFor(int score) {
    if (score < 1 || score > 22) {
      throw new IllegalArgumentException("Attempt to get score for " + score);
    }

    return score;
  }

  private long getSum(User u) {
    long sum = 0;

    for (int i = 0; i < count.size(); i++) {
      if (count.get(i).equals(u)) {
        sum += pointsFor(i + 1);
      }
    }

    if (u.equals(loser)) {
      sum += pointsFor(count.size() + 1);
    }

    return sum;
  }

  private Optional<User> getLastUser() {
    return count.isEmpty() ? Optional.empty() : Optional.of(count.get(count.size() - 1));
  }
}
