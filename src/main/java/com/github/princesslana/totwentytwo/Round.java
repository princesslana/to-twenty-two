package com.github.princesslana.totwentytwo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Round {

  private static final Logger LOG = LoggerFactory.getLogger(Round.class);

  private List<User> count = new ArrayList<>();

  private User loser;

  public void onMessage(User who, String msg) {
    if (isDone() || (count.isEmpty() && !msg.equals("1"))) {
      return;
    }

    boolean isExpectedNumber = msg.equals(getExpectedCount().toString());
    boolean isSameAsLastUser = Optional.of(who).equals(getLastUser());

    if (isExpectedNumber && !isSameAsLastUser) {
      LOG.info("{} got {}", who.getTag(), getExpectedCount());
      count.add(who);
    } else {
      LOG.info("{} lost", who.getTag());
      loser = who;
    }
  }

  public boolean isDone() {
    return loser != null || count.size() == 22;
  }

  public Result getResult() {
    if (!isDone()) {
      throw new IllegalStateException("Can't get result of unfinished round");
    }

    ImmutableResult.Builder r = ImmutableResult.builder();

    Map<User, Long> scores = new HashMap<>();

    if (loser == null) {
      User got22 = count.get(21);
      User got21 = count.get(20);
      r = r.winner(got22);

      getPlayers().stream().forEach(p -> scores.put(p, -getSum(p)));

      if (getPlayers().size() > 2) {
        scores.put(got21, 0L);
      }

      scores.put(got22, getSum(got22));
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

  private long getSum(User u) {
    long sum = 0;

    for (int i = 0; i < count.size(); i++) {
      if (count.get(i).equals(u)) {
        sum += i + 1;
      }
    }

    if (u.equals(loser)) {
      sum += count.size() + 1;
    }

    return sum;
  }

  private Optional<User> getLastUser() {
    return count.isEmpty() ? Optional.empty() : Optional.of(count.get(count.size() - 1));
  }
}

