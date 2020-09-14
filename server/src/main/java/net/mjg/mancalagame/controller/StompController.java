package net.mjg.mancalagame.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.mjg.mancalagame.game.GameJudge;
import net.mjg.mancalagame.game.GameMove;
import net.mjg.mancalagame.game.GameState;
import net.mjg.mancalagame.game.Score;
import net.mjg.mancalagame.messages.*;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class StompController {

    // todo thread safe
    private GameState game = new GameState();
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messageSending;
    private final GameJudge judge = new GameJudge();

    public StompController(ObjectMapper objectMapper, SimpMessageSendingOperations messageSending){
        this.objectMapper = objectMapper;
        this.messageSending = messageSending;
    }
    
    @MessageExceptionHandler(InvalidMessageException.class)
    public void handleException(@Header("client-id")String clientId, InvalidMessageException exception) {
        if(clientId == null || clientId.equals("")) {
            throw new RuntimeException("Can't handle exception for a message without clientId");
        }
        messageSending.convertAndSend("/game/error/"+clientId, new ErrorMessage(exception.getMessage()));
    }

    @MessageMapping("/game/new")
    @SendTo("/game/state")
    public GameStateMessage newGame() {
        game = new GameState();
        return currentStateMessage();
    }

    @MessageMapping("/game/join")
    @SendTo("/game/state")
    public GameStateMessage joinGame() {
        return currentStateMessage();
    }

    @MessageMapping("/game/move")
    @SendTo("/game/state")
    public GameStateMessage move(MoveMessage message) throws InvalidMessageException {
        game.applyMove(new GameMove(message.getPlayer(), message.getFrom()));

        return currentStateMessage();
    }

    private GameStateMessage currentStateMessage() {
        return new GameStateMessage(game, judge.score(game));
    }
}