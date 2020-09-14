package net.mjg.mancalagame.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.mjg.mancalagame.game.GameJudge;
import net.mjg.mancalagame.game.GameMove;
import net.mjg.mancalagame.game.GameState;
import net.mjg.mancalagame.game.Score;
import net.mjg.mancalagame.messages.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketController {
    private final ConcurrentHashMap<String, GameStateStorage> games = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final GameJudge judge = new GameJudge();


    public WebSocketController(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }
    
    public void handleJoin(JoinGameMessage message, String gameId, WsConnection connection) throws IOException, InvalidMessageException {
        GameStateStorage gameStorage = games.get(gameId);
        if(gameStorage == null) {
            throw new InvalidMessageException("Invalid game id: "+gameId);
        }
        if(gameStorage.bluePlayer != null) {
            throw new InvalidMessageException("Game already full");
        }
        gameStorage.bluePlayer = connection;
//        connection.sendMessage(new GameStateMessage(gameStorage.game));
    }

    public void handleNewGame(NewGameMessage message, String gameId, WsConnection connection) throws IOException {
        GameState game = new GameState();
        GameStateStorage gameStorage = new GameStateStorage();
        gameStorage.game = game;
        gameStorage.redPlayer = connection;
        games.put(gameId, gameStorage);
//        connection.sendMessage(new GameStateMessage(game));
    }

    public void handleMove(MoveMessage message, String gameId, WsConnection connection) throws IOException, InvalidMessageException {
        GameStateStorage gameStorage = games.get(gameId);
        if(gameStorage == null) {
            throw new InvalidMessageException("Invalid game id: "+gameId);
        }
        GameState game = gameStorage.game;
        // todo validate moving player
        game.applyMove(new GameMove(game.getCurrentPlayer(), message.getFrom()));
        
//        sendToAll(new GameStateMessage(game), gameStorage);
        
        Score score = judge.score(game);
        if(score.isFinished()) {
            sendToAll(new GameFinishedMessage(score), gameStorage);
        }
    }
    
    private void sendToAll(Message message, GameStateStorage gameStateStorage) throws IOException {
        gameStateStorage.bluePlayer.sendMessage(message);
        gameStateStorage.redPlayer.sendMessage(message);
    }

    static class GameStateStorage {
        public GameState game;
        public WsConnection redPlayer;
        public WsConnection bluePlayer;
    }
}

