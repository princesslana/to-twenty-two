package com.github.princesslana.totwentytwo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import org.immutables.value.Value;

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

    str.append("```\n");
    str.append("THIS ROUND:");
    getScores().stream()
        .sorted(Comparator.comparing(Score::getScore).reversed())
        .forEach(
            sc -> str.append(String.format("\n%10d : %s", sc.getScore(), sc.getUser().getTag())));
    str.append("\n```");

    return str.toString();
  }
}
