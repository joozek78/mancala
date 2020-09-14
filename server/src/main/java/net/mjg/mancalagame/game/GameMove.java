package net.mjg.mancalagame.game;

import lombok.Data;

@Data
public class GameMove {
    private final Player player;
    private final int from;
}
