package net.mjg.mancalagame.controller;

import net.mjg.mancalagame.game.GameState;
import net.mjg.mancalagame.game.exceptions.InvalidMoveException;

@FunctionalInterface
public interface GameMutator {
    GameState mutate(GameState gameState) throws InvalidMoveException;
}
