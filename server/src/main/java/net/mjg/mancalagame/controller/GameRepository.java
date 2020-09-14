package net.mjg.mancalagame.controller;

import net.mjg.mancalagame.game.GameState;
import net.mjg.mancalagame.game.exceptions.InvalidMoveException;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class GameRepository {
    private GameState game = new GameState();
    private final ReadWriteLock gameLock = new ReentrantReadWriteLock();

    public void mutateGame(GameMutator operation) throws InvalidMoveException {
        var lock = gameLock.writeLock();
        try {
            lock.lock();
            game = operation.mutate(game);
        } finally {
            lock.unlock();
        }
    }
    
    public GameState getGame() {
        var lock = gameLock.readLock();
        try {
            lock.lock();
            return new GameState(game);
        } finally {
            lock.unlock();
        }
    }
}

