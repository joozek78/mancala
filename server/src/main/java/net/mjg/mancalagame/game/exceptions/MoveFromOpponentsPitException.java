package net.mjg.mancalagame.game.exceptions;

import net.mjg.mancalagame.controller.InvalidMessageException;

public class MoveFromOpponentsPitException extends InvalidMessageException {
    public MoveFromOpponentsPitException() {
        super("Can't move from opponent's pit");
    }
}
