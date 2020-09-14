package net.mjg.mancalagame.game;

import lombok.Getter;

class GameStateBuilder {
    
    @Getter
    private Player startingPlayer = Player.Red;

    private final int[] slots = new int[GameState.TOTAL_NUMBER_OF_SLOTS];

    public GameStateBuilder setStartingPlayer(Player player) {
        startingPlayer = player;
        return this;
    }

    /**
     * Adds a number of stones to a red pit.
     * @param slot index of the red pit to add stones to.
     * @param stones number of stones to add.
     */
    public GameStateBuilder addStonesToRed(int slot, int stones) {
        slots[slot] += stones;
        return this;
    }

    /**
     * Adds a number of stones to a blue pit.
     * @param slot index of the blue pit to add stones to. Zero means the first blue pit.
     * @param stones number of stones to add.
     */
    public GameStateBuilder addStonesToBlue(int slot, int stones) {
        slots[GameState.RED_STORE_ID + 1 + slot] += stones;
        return this;
    }

    public GameStateBuilder addStonesToRedStore(int stones) {
        slots[GameState.RED_STORE_ID] += stones;
        return this;
    }

    public GameStateBuilder addStonesToBlueStore(int stones) {
        slots[GameState.BLUE_STORE_ID] += stones;
        return this;
    }

    public GameState build() {
        return new GameState(startingPlayer, slots);
    }
}
