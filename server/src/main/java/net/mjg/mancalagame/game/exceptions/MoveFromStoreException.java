package net.mjg.mancalagame.game.exceptions;

import net.mjg.mancalagame.controller.InvalidMessageException;

public class MoveFromStoreException extends InvalidMessageException {
    public MoveFromStoreException() {
        super("Can't move from a store");
    }
}
