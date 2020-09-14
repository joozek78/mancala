package net.mjg.mancalagame.game.exceptions;

import net.mjg.mancalagame.controller.InvalidMessageException;

public class MoveFromInvalidSlotId extends InvalidMessageException {

    public MoveFromInvalidSlotId(String message) {
        super(message);
    }
}
