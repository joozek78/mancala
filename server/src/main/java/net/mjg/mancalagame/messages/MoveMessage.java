package net.mjg.mancalagame.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.mjg.mancalagame.game.Player;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MoveMessage implements Message {
    private int from;
    private Player player;
}
