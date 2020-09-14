package net.mjg.mancalagame.game;

public enum Player {
    Red, Blue;

    public Player opponent() {
        return this == Red ? Blue : Red;
    }
}
