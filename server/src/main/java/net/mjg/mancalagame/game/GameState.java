package net.mjg.mancalagame.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import net.mjg.mancalagame.controller.InvalidMessageException;
import net.mjg.mancalagame.game.exceptions.*;

import java.util.Arrays;
import java.util.Objects;

@JsonDeserialize(builder = GameState.GameStateBuilder.class)
@Builder
public class GameState {
    
    public static final int PITS_PER_PLAYER = 6;
    public static final int TOTAL_NUMBER_OF_SLOTS = PITS_PER_PLAYER * 2 + 2;
    public static final int RED_STORE_ID = PITS_PER_PLAYER;
    public static final int BLUE_STORE_ID = PITS_PER_PLAYER * 2 + 1;
    
    @Getter
    private Player currentPlayer;
    
    @JsonProperty("slots")
    private final int[] stones;

    public GameState() {
        this.currentPlayer = Player.Red;
        // pits + stores
        this.stones = new int[TOTAL_NUMBER_OF_SLOTS];
        for(int slot = 0; slot < PITS_PER_PLAYER; ++slot) {
            stones[slot] = 4;
        }
        for(int slot = RED_STORE_ID + 1; slot < BLUE_STORE_ID; ++slot) {
            stones[slot] = 4;
        }
    }
    
    public GameState(Player currentPlayer, int... slots){
        if(currentPlayer == null) {
            throw new IllegalArgumentException("currentPlayer should not be null");
        }
        if(slots == null) {
            throw new IllegalArgumentException("slots should not be null");
        }
        if(slots.length != TOTAL_NUMBER_OF_SLOTS) {
            throw new IllegalArgumentException(String.format("slots should be exactly %s long", TOTAL_NUMBER_OF_SLOTS));
        }
        
        this.currentPlayer = currentPlayer;
        this.stones = slots.clone();
    }

    public int getStonesBySlotId(int slot) {
        return stones[slot];
    }
    
    @JsonProperty("slots")
    public int[] getAllStones() {
        return stones.clone();
    }
    
    public void applyMove(GameMove move) throws InvalidMessageException {
        if(move.getPlayer() != currentPlayer) {
            throw new MoveOutOfOrderException();
        }
        var from = move.getFrom();
        if(from < 0 || from >= TOTAL_NUMBER_OF_SLOTS) {
            throw new MoveFromInvalidSlotId(String.format("Slot id %s is invalid", from));
        }
        if(from == RED_STORE_ID || from == BLUE_STORE_ID) {
            throw new MoveFromStoreException();
        }
        if(move.getPlayer() == Player.Red && from > RED_STORE_ID || move.getPlayer() == Player.Blue && from <RED_STORE_ID) {
            throw new MoveFromOpponentsPitException();
        }
        
        int payload = stones[from];
        if(payload == 0) {
            throw new MoveFromEmptyPitException();
        }
        stones[from] = 0;

        int myStore = move.getPlayer() == Player.Red ? RED_STORE_ID : BLUE_STORE_ID;
        int opponentStore = move.getPlayer() == Player.Red ? BLUE_STORE_ID : RED_STORE_ID;
        int targetSlot = from;
        while(payload > 0){
            targetSlot++;
            if(targetSlot == opponentStore) {
                targetSlot++;
            }
            if(targetSlot == TOTAL_NUMBER_OF_SLOTS) {
                targetSlot = 0;
            }
            
            stones[targetSlot]++;
            payload--;
        }

        resolveCapture(move, myStore, targetSlot);
        if(targetSlot != myStore) {
            currentPlayer = currentPlayer.opponent();
        }
    }

    private void resolveCapture(GameMove move, int myStore, int targetSlot) {
        if(stones[targetSlot] == 1 && isOneOfMyPits(move.getPlayer(), targetSlot)) {
            int oppositeSlot = TOTAL_NUMBER_OF_SLOTS - 2 - targetSlot;
            stones[myStore] += stones[targetSlot] + stones[oppositeSlot];
            stones[targetSlot] = 0;
            stones[oppositeSlot] = 0;
        }
    }

    private boolean isOneOfMyPits(Player me, int slot) {
        if(me == Player.Red) {
            return slot < RED_STORE_ID;
        } else {
            return slot > RED_STORE_ID && slot < BLUE_STORE_ID;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameState gameState = (GameState) o;
        return currentPlayer == gameState.currentPlayer &&
                Arrays.equals(stones, gameState.stones);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(currentPlayer);
        result = 31 * result + Arrays.hashCode(stones);
        return result;
    }
    
    @JsonPOJOBuilder(withPrefix = "")
    public static class GameStateBuilder { }
}

