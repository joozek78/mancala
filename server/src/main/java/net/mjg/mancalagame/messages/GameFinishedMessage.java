package net.mjg.mancalagame.messages;

import lombok.*;
import net.mjg.mancalagame.game.Score;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GameFinishedMessage implements Message {
    private Score score;
}
