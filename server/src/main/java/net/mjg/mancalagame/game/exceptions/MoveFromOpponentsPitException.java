package net.mjg.mancalagame.game.exceptions;

public class MoveFromOpponentsPitException extends InvalidMoveException {
    public MoveFromOpponentsPitException() {
        super("Can't move from opponent's pit");
    }
}
