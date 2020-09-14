package net.mjg.mancalagame.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.mjg.mancalagame.game.GameState;
import net.mjg.mancalagame.game.Score;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GameStateMessage implements Message {
    private GameState gameState;
    private Score score;
}
