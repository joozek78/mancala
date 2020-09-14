package net.mjg.mancalagame.game.exceptions;

import net.mjg.mancalagame.controller.InvalidMessageException;

public class MoveOutOfOrderException extends InvalidMessageException {
    public MoveOutOfOrderException() {
        super("Can't move on opponent's turn");
    }
}

