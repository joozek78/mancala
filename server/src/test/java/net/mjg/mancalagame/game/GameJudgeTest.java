package net.mjg.mancalagame.game;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameJudgeTest {

    private GameJudge judge;

    @BeforeEach
    public void setUp(){
        judge = new GameJudge();
    }
    
    @Test
    public void countsRedStones(){
        GameState game = new GameStateBuilder()
                .addStonesToBlue(1, 10)
                .addStonesToBlueStore(10)
                .addStonesToRed(0, 1)
                .addStonesToRed(1, 2)
                .addStonesToRedStore(3)
                .build();

        Score score = judge.score(game);

        Assertions.assertThat(score.getRedScore()).isEqualTo(6);
    }
    
    @Test
    public void countsBlueStones(){
        GameState game = new GameStateBuilder()
                .addStonesToRed(1, 10)
                .addStonesToRedStore(10)
                .addStonesToBlue(0, 1)
                .addStonesToBlue(1, 2)
                .addStonesToBlueStore(3)
                .build();

        Score score = judge.score(game);

        Assertions.assertThat(score.getBlueScore()).isEqualTo(6);
    }
    
    @Test
    public void whenBothSidesHaveStonesTheGameIsInProgress(){
        GameState game = new GameStateBuilder()
                .addStonesToRed(0, 1)
                .addStonesToBlue(0, 3)
                .build();

        Score score = judge.score(game);

        Assertions.assertThat(score.isFinished()).isFalse();
    }

    @Test
    public void whenRedHasNoStonesInPitsTheGameIsFinished(){
        GameState game = new GameStateBuilder()
                .addStonesToRedStore(1)
                .addStonesToBlue(1, 3)
                .build();

        Score score = judge.score(game);

        Assertions.assertThat(score.isFinished()).isTrue();
    }

    @Test
    public void whenBlueHasNoStonesInPitsTheGameIsFinished(){
        GameState game = new GameStateBuilder()
                .addStonesToBlueStore(1)
                .addStonesToRed(1, 3)
                .build();

        Score score = judge.score(game);

        Assertions.assertThat(score.isFinished()).isTrue();
    }
}
