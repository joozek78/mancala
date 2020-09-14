package net.mjg.mancalagame.game.exceptions;

import net.mjg.mancalagame.controller.InvalidMessageException;

public class MoveFromEmptyPitException extends InvalidMessageException {
    public MoveFromEmptyPitException() {
        super("Can't move from an empty pit");
    }
}

