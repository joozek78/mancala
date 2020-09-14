package net.mjg.mancalagame.game.exceptions;

public class MoveFromEmptyPitException extends InvalidMoveException {
    public MoveFromEmptyPitException() {
        super("Can't move from an empty pit");
    }
}

