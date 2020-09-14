package net.mjg.mancalagame.game;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Represents a score for an in-progress or finished game.
 */
@Data
@AllArgsConstructor
@JsonDeserialize(builder = Score.ScoreBuilder.class)
@Builder
public class Score {
    /**
     * If the game is finished or still in progress.
     */
    private final boolean finished;

    /**
     * Number of stones that the red player has on their side. 
     */
    private final int redScore;

    /**
     * Number of stones that the blue player has on their side. 
     */
    private final int blueScore;
    
    @JsonPOJOBuilder(withPrefix = "")
    public static class ScoreBuilder {}
}

