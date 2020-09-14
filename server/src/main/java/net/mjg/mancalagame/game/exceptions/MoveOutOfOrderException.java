package net.mjg.mancalagame.game.exceptions;

public class MoveOutOfOrderException extends InvalidMoveException {
    public MoveOutOfOrderException() {
        super("Can't move on opponent's turn");
    }
}

