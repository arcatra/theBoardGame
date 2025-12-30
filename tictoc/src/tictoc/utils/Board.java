package tictoc.utils;

// Imports ---------------
import java.util.ArrayList;

import tictoc.TicToc;
// -----------------------

public class Board {
    TicToc gameObj;

    private ArrayList<BoardBox[]> board = new ArrayList<>();
    private int rows;
    private int columns;

    public Board(int rowSize, int columnSize, TicToc gameObj) {
        this.rows = rowSize;
        this.columns = columnSize;
        this.gameObj = gameObj;

    }

    public void buildBoard() {
        System.out.println("Trying to build the board");
        int boxId = 1;
        for (int row = 0; row < this.rows; row++) {
            BoardBox[] boxes = new BoardBox[this.columns];

            for (int column = 0; column < this.columns; column++) {
                boxes[column] = new BoardBox(Integer.toString(boxId), TicToc.RESET);
                gameObj.boxMap.put(boxId, new int[] { row, column });

                boxId++;
            }

            this.board.add(boxes);
        }

        System.out.printf(
                "Success, constructed board with %d rows and %d columns\n\n", this.rows, this.columns);
    }

    public void displayBoard() {
        String boxString = "|  0  |";
        int repeatCount = boxString.length() - 2;

        for (BoardBox[] rowValues : this.board) {
            System.out.println(("+" + "-".repeat(repeatCount)).repeat(this.columns) + "+");
            for (BoardBox box : rowValues) {
                String boxSymbol = box.getSymbol();
                String symbolColor = box.getSymbolColor();

                boxString = (boxSymbol.length() < 2 ? "|  " : "| ") + symbolColor + boxSymbol + TicToc.RESET
                        + "  ";

                System.out.print(boxString);
            }
            System.out.println("|");
        }
        System.out.println(("+" + "-".repeat(repeatCount)).repeat(this.columns) + "+");

    }

    public BoardBox getBoardBox(int currRow, int currColumn) {
        BoardBox box = this.board.get(currRow)[currColumn];

        return box;
    }
}
