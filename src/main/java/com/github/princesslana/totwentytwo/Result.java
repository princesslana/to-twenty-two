package com.github.princesslana.totwentytwo;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import org.immutables.value.Value;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableResult.class)
public interface Result {
  Optional<User> getWinner();
  Optional<User> getLoser();
  Set<Score> getScores();

  default String format() {
    StringBuilder str = new StringBuilder();

    getWinner().ifPresent(w -> str.append("**" + w.getTag() + "** won!\n"));
    getLoser().ifPresent(l -> str.append("**" + l.getTag() + "** lost!\n"));

    str.append("\n**Scores:**");
    getScores()
      .stream()
      .sorted(Comparator.comparing(Score::getScore).reversed())
      .forEach(sc -> str.append("\n" + sc.getUser().getTag() + ": " + sc.getScore()));

    return str.toString();
  }

}
