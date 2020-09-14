package net.mjg.mancalagame.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.mjg.mancalagame.game.GameJudge;
import net.mjg.mancalagame.game.GameMove;
import net.mjg.mancalagame.game.GameState;
import net.mjg.mancalagame.game.exceptions.InvalidMoveException;
import net.mjg.mancalagame.messages.ErrorMessage;
import net.mjg.mancalagame.messages.GameStateMessage;
import net.mjg.mancalagame.messages.MoveMessage;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    private final SimpMessageSendingOperations messageSending;
    private final GameRepository gameRepository;
    private final GameJudge judge = new GameJudge();

    public GameController(SimpMessageSendingOperations messageSending, GameRepository gameRepository){
        this.messageSending = messageSending;
        this.gameRepository = gameRepository;
    }
    
    @MessageExceptionHandler(InvalidMoveException.class)
    public void handleException(@Header("client-id")String clientId, InvalidMoveException exception) {
        if(clientId == null || clientId.equals("")) {
            throw new RuntimeException("Can't handle exception for a message without clientId");
        }
        messageSending.convertAndSend("/game/error/"+clientId, new ErrorMessage(exception.getMessage()));
    }

    @MessageMapping("/game/new")
    @SendTo("/game/state")
    public GameStateMessage newGame() throws InvalidMoveException {
        gameRepository.mutateGame(g -> new GameState());
        return currentStateMessage();
    }

    @MessageMapping("/game/join")
    @SendTo("/game/state")
    public GameStateMessage joinGame() {
        return currentStateMessage();
    }

    @MessageMapping("/game/move")
    @SendTo("/game/state")
    public GameStateMessage move(MoveMessage message) throws InvalidMoveException {
        gameRepository.mutateGame(g -> {
            g.applyMove(new GameMove(message.getPlayer(), message.getFrom()));
            return g;
        });

        return currentStateMessage();
    }

    private GameStateMessage currentStateMessage() {
        var game = gameRepository.getGame();
        return new GameStateMessage(game, judge.score(game));
    }
}

