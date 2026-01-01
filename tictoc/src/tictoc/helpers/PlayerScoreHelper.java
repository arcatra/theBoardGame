package tictoc.helpers;

import tictoc.utils.Board;
import tictoc.utils.BoardBox;

public class PlayerScoreHelper {
    private int columns;
    private Board board;

    public PlayerScoreHelper(int columnSize, Board board) {
        this.board = board;
        this.columns = columnSize;

    }

    public int getCurrPlayerPoints(int row, int column, String boxSymbol) {
        int points = 0;
        int beyond;

        if (boxSymbol.equals("X")) {
            beyond = 2;
            // for horizontal
            points += this.getValidPoints(row, column, beyond, false, "XOX");

            // for vertical
            points += this.getValidPoints(column, row, beyond, true, "XOX");

        } else if (boxSymbol.equals("O")) {
            beyond = 1;
            // Horizontal
            points += this.getValidPoints(row, column, beyond, false, "XOX");

            // Vertical
            points += this.getValidPoints(column, row, beyond, true, "XOX");

        }

        return points;
    }

    private int[] getValidRange(int columnValue, int beyond) {
        int start = Math.max(0, columnValue - beyond);
        int end = Math.min(columnValue + beyond, (this.columns - 1));

        return new int[] { start, end };
    }

    private int getValidPoints(int row, int column, int beyond, boolean swap, String match) {
        int validPoints = 0;

        int[] range = this.getValidRange(column, beyond);
        StringBuilder validString = new StringBuilder("");

        // System.out.printf("Start: %d, end: %d\n", range[0], range[1]);

        for (int index = range[0]; index <= range[1]; index++) {
            BoardBox box = swap ? this.board.getBoardBox(index, row) : this.board.getBoardBox(row, index);

            if ("XO".contains(box.getSymbol())) {
                validString.append(box.getSymbol());

            } else {
                validString.setLength(0);

            }

            if (validString.length() == match.length()) {
                if (validString.toString().equals(match)) {
                    validPoints++;
                    validString.setLength(1);

                } else {
                    validString.deleteCharAt(0);

                }
            }

        }

        return validPoints;
    }

}
