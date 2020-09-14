package net.mjg.mancalagame.game;

import net.mjg.mancalagame.controller.InvalidMessageException;
import net.mjg.mancalagame.game.exceptions.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class GameTest {
    
    @Test
    public void whenPlayerMakesAMove_theySowTheirStonesInUpcomingPits() throws InvalidMessageException {
        GameState game = new GameStateBuilder()
                .addStonesToRed(0, 3)
                .addStonesToRed(1, 1)
                .addStonesToRed(2, 1)
                .addStonesToRed(3, 1)
                .build();
        
        game.applyMove(new GameMove(Player.Red, 0));
        
        assertStonesEqual(game, new GameStateBuilder()
        .addStonesToRed(1, 2)
        .addStonesToRed(2, 2)
        .addStonesToRed(3, 2)
        .build());
    }
    @Test
    public void whenPlayerEndsInTheirStore_theyGetAnotherMove() throws InvalidMessageException {
        GameState game = new GameStateBuilder()
                .setStartingPlayer(Player.Red)
                .addStonesToRed(4, 2)
                .build();
        
        game.applyMove(new GameMove(Player.Red, 4));

        assertStonesEqual(game, new GameStateBuilder()
        .addStonesToRed(5, 1)
        .addStonesToRedStore(1)
        .build());
        assertThat(game.getCurrentPlayer()).isEqualTo(Player.Red);
    }
    
    @Test
    public void whenPlayerMovesFromAnEmptyPit_exceptionIsThrown(){
        GameState game = new GameStateBuilder()
                .setStartingPlayer(Player.Red)
                .addStonesToRed(4, 2)
                .build();
        Assertions.assertThatThrownBy(() -> game.applyMove(new GameMove(Player.Red, 0)))
                .isInstanceOf(MoveFromEmptyPitException.class);
    }
    
    @Test
    public void whenPlayerMovesOutOfOrder_exceptionIsThrown(){
        GameState game = new GameStateBuilder()
                .setStartingPlayer(Player.Blue)
                .addStonesToBlue(4, 2)
                .build();
        Assertions.assertThatThrownBy(() -> game.applyMove(new GameMove(Player.Red, 4)))
                .isInstanceOf(MoveOutOfOrderException.class);
    }
    
    @Test
    public void whenPlayerMovesFromAStore_exceptionIsThrown(){
        GameState game = new GameStateBuilder()
                .setStartingPlayer(Player.Blue)
                .addStonesToBlueStore(4)
                .build();
        Assertions.assertThatThrownBy(() -> game.applyMove(new GameMove(Player.Blue, GameState.BLUE_STORE_ID)))
                .isInstanceOf(MoveFromStoreException.class);
    }
    
    @Test
    public void whenPlayerMovesFromOutOfRange_exceptionIsThrown(){
        GameState game = new GameStateBuilder()
                .setStartingPlayer(Player.Red)
                .addStonesToBlueStore(4)
                .build();
        Assertions.assertThatThrownBy(() -> game.applyMove(new GameMove(Player.Red, -1)))
                .isInstanceOf(MoveFromInvalidSlotId.class);
    }
    
    @Test
    public void whenPlayerMovesFromOpponentsPit_exceptionIsThrown(){
        GameState game = new GameStateBuilder()
                .setStartingPlayer(Player.Red)
                .addStonesToBlueStore(4)
                .build();
        Assertions.assertThatThrownBy(() -> game.applyMove(new GameMove(Player.Red, bluePitToAbsolute(0))))
                .isInstanceOf(MoveFromOpponentsPitException.class);
    }
    
    @Test
    public void whenRedPassesOwnStore_theyAddStonesToBluePits() throws InvalidMessageException {
        GameState game = new GameStateBuilder()
                .addStonesToRed(4, 3)
                .build();

        game.applyMove(new GameMove(Player.Red, 4));

        assertStonesEqual(game, new GameStateBuilder()
                .addStonesToRed(5, 1)
                .addStonesToRedStore(1)
                .addStonesToBlue(0, 1)
                .build());
    }
    
    @Test
    public void whenBluePassesOwnStore_theyAddStonesToRedPits() throws InvalidMessageException {
        GameState game = new GameStateBuilder()
                .addStonesToBlue(5, 3)
                .setStartingPlayer(Player.Blue)
                .build();

        game.applyMove(new GameMove(Player.Blue, bluePitToAbsolute(5)));

        assertStonesEqual(game, new GameStateBuilder()
                .addStonesToBlueStore(1)
                .addStonesToRed(0, 1)
                .addStonesToRed(1, 1)
                .build());
    }
    
    @Test
    public void whenRedLandsInOwnEmptyPit_theyCaptureOpposingPit() throws InvalidMessageException {
        GameState game = new GameStateBuilder()
                .addStonesToRed(1, 2)
                .addStonesToBlue(2, 3) // this slot is opposite to 3
                //   5 4 3 2 1 0
                // 6   > > X     6
                //   0 1 2 3 4 5
                .build();

        game.applyMove(new GameMove(Player.Red, 1));

        assertStonesEqual(game, new GameStateBuilder()
                .addStonesToRed(2, 1)
                // captured
                .addStonesToRedStore(4)
                .build());
    }
    
    @Test
    public void whenBlueLandsInOwnEmptyPit_theyCaptureOpposingPit() throws InvalidMessageException {
        GameState game = new GameStateBuilder()
                .addStonesToBlue(1, 2)
                .addStonesToRed(2, 3) // this slot is opposite to 3
                //   5 4 3 2 1 0
                // 6     X < <   6
                //   0 1 2 3 4 5
                .setStartingPlayer(Player.Blue)
                .build();

        game.applyMove(new GameMove(Player.Blue, bluePitToAbsolute(1)));

        assertStonesEqual(game, new GameStateBuilder()
                .addStonesToBlue(2, 1)
                // captured
                .addStonesToBlueStore(4)
                .build());
    }
    
    @Test
    public void whenRedPassesOpponentsStore_theySkipIt() throws InvalidMessageException {
        int lastPit = GameState.PITS_PER_PLAYER - 1;
        GameState game = new GameStateBuilder()
                .addStonesToRed(0, 1)
                .addStonesToRed(lastPit, 8)
                .build();

        game.applyMove(new GameMove(Player.Red, lastPit));

        assertStonesEqual(game, new GameStateBuilder()
                .addStonesToRedStore(1)
                .addStonesToBlue(0, 1)
                .addStonesToBlue(1, 1)
                .addStonesToBlue(2, 1)
                .addStonesToBlue(3, 1)
                .addStonesToBlue(4, 1)
                .addStonesToBlue(5, 1)
                // blue store should be skipped
                .addStonesToRed(0, 2)
                .build());
    }
    
    @Test
    public void whenBluePassesOpponentsStore_theySkipIt() throws InvalidMessageException {
        int lastPit = GameState.PITS_PER_PLAYER - 1;
        GameState game = new GameStateBuilder()
                .addStonesToBlue(lastPit, 8)
                .addStonesToBlue(0, 1)
                .setStartingPlayer(Player.Blue)
                .build();
        
        game.applyMove(new GameMove(Player.Blue, bluePitToAbsolute(lastPit)));

        assertStonesEqual(game, new GameStateBuilder()
                .addStonesToBlueStore(1)
                .addStonesToRed(0, 1)
                .addStonesToRed(1, 1)
                .addStonesToRed(2, 1)
                .addStonesToRed(3, 1)
                .addStonesToRed(4, 1)
                .addStonesToRed(5, 1)
                // red store should be skipped
                .addStonesToBlue(0, 2)
                .build());
    }

    /**
     * Translates an index of a blue pit to absolute slot number used by moves.
     */
    private int bluePitToAbsolute(int bluePitIndex){
        return bluePitIndex + GameState.RED_STORE_ID + 1;
    }
    
    private void assertStonesEqual(GameState actual, GameState expected) {
        if(!Arrays.equals(actual.getAllStones(), expected.getAllStones())) {
            Assertions.fail("Game assertion failed. Expected: \n%s, but found\n%s",
                    GameTextFormatter.format(expected),
                    GameTextFormatter.format(actual));
        }
    }

}
