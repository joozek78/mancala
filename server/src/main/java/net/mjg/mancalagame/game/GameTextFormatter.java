package net.mjg.mancalagame.game;

public class GameTextFormatter {
    /**
     * Returns ASCII-art representation of the game state. Includes only numbers of stones in slots, not who's the current player.
     * Renders Red player on the bottom and Blue on top. Example representation where the Blue store has
     * 10 stones and the Red store has 5:
     * +--+-----------+--+
     * |10|4 3 2 0 0 1|  |
     * |  |0 0 1 2 4 1| 5|
     * +--+-----------+--+
     */
    public static String format(GameState game) {
        StringBuilder sb = new StringBuilder();
        writeDelimiter(sb);
        writeBlue(game, sb);
        writeRed(game, sb);
        writeDelimiter(sb);

        return sb.toString();
    }

    private static void writeBlue(GameState game, StringBuilder sb) {
        sb.append(String.format("|%2s|", game.getStonesBySlotId(GameState.BLUE_STORE_ID)));
        for (int pit = GameState.BLUE_STORE_ID - 1; pit > GameState.PITS_PER_PLAYER; pit--) {
            sb.append(String.format("%2s", game.getStonesBySlotId(pit)));
        }
        sb.append("|  |\n");
    }

    private static void writeRed(GameState game, StringBuilder sb) {
        sb.append("|  |");
        for (int pit = 0; pit < GameState.RED_STORE_ID; pit++) {
            sb.append(String.format("%2s", game.getStonesBySlotId(pit)));
        }
        sb.append(String.format("|%2s|\n", game.getStonesBySlotId(GameState.RED_STORE_ID)));
    }

    private static void writeDelimiter(StringBuilder sb) {
        sb.append("+--+").append("-".repeat(GameState.PITS_PER_PLAYER * 2)).append("+--+\n");
    }
}
