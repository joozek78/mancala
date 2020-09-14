package net.mjg.mancalagame.game;

import lombok.Data;

import java.util.Arrays;

public class GameJudge {
    
    public Score score(GameState game) {
        int[] allStones = game.getAllStones();
        
        int redPits = Arrays.stream(allStones)
                .limit(GameState.PITS_PER_PLAYER)
                .sum();
        int redScore = redPits + game.getStonesBySlotId(GameState.RED_STORE_ID);
        
        int bluePits = Arrays.stream(allStones)
                .skip(GameState.PITS_PER_PLAYER + 1)
                .limit(GameState.PITS_PER_PLAYER)
                .sum();
        int blueScore = bluePits + game.getStonesBySlotId(GameState.BLUE_STORE_ID);
        
        return new Score(redPits == 0 || bluePits == 0, redScore, blueScore);
    }
}
