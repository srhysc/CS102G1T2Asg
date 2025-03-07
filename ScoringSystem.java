import java.util.*;

public class ScoringSystem {
    public static int calculateScore(Player player) {
        Map<String, Integer> suitCount = new HashMap<>();
        for (Card card : player.getCollected()) {
            suitCount.put(card.getColour(), suitCount.getOrDefault(card.getColour(), 0) + 1);
        }

        int score = 0;
        for (Card card : player.getCollected()) {
            if (Collections.max(suitCount.values()) > suitCount.get(card.getColour())) {
                score += card.getValue();
            } else {
                score += 1;
            }
        }
        return score;
    }
}
