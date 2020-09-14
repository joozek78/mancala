package net.mjg.mancalagame.game.exceptions;

public class MoveFromStoreException extends InvalidMoveException {
    public MoveFromStoreException() {
        super("Can't move from a store");
    }
}
