package com.github.princesslana.totwentytwo;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class History {

  private List<Result> results = new ArrayList<>();

  private File file;

  public void add(Result r) {
    results.add(r);

    try {
      Config.getJackson().writeValue(file, this);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Set<Score> getTotalScores() {
    Map<User, Long> scores = new HashMap<>();

    results
        .stream()
        .flatMap(r -> r.getScores().stream())
        .forEach(
            s -> {
              if (!scores.containsKey(s.getUser())) {
                scores.put(s.getUser(), 0L);
              }
              scores.put(s.getUser(), scores.get(s.getUser()) + s.getScore());
            });

    return scores
        .entrySet()
        .stream()
        .map(e -> ImmutableScore.builder().user(e.getKey()).score(e.getValue()).build())
        .collect(Collectors.toSet());
  }

  public String leaderboard() {
    StringBuilder str = new StringBuilder();

    str.append("**Leaderboard:**");
    getTotalScores()
        .stream()
        .sorted(Comparator.comparing(Score::getScore).reversed())
        .forEach(sc -> str.append("\n" + sc.getUser().getTag() + ": " + sc.getScore()));

    return str.toString();
  }

  public static History load(String channelId) {
    File f = new File(Config.getHistoryFolder(), channelId + ".json");

    try {
      History history =
          f.exists() ? Config.getJackson().readValue(f, History.class) : new History();
      history.file = f;
      return history;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
