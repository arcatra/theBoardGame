package tictoc.helpers;

// Imports ------------------
import java.util.Scanner;

import tictoc.TicToc;
import tictoc.utils.BoardBox;
import tictoc.utils.Player;
// -------------------------

public class GameLoopHelper {

    public String readSymbol(Scanner input) {
        while (true) {
            System.out.print("\nEnter the chosen symbol: ");
            String symbol = input.nextLine().toUpperCase();

            if ("XO".contains(symbol) || "XOX".equals(symbol)) {
                return symbol;

            } else {
                System.out.println("\nOnly symbols 'x', 'o' or 'xox'(exit) are allowed, try again\n");

            }
        }
    }

    public int readBoxId(Scanner input, int rowSize, int columnSize) {
        int id;
        while (true) {
            System.out.print("Enter the choosen box ID: ");
            try {
                id = Integer.parseInt(input.nextLine());

                if (id <= (rowSize * columnSize) && id > 0)
                    return id;

                System.out.printf("\nInvalid box id, should be <=%d and >=1, Please try again\n\n",
                        (rowSize * columnSize));

            } catch (NumberFormatException e) {
                System.out.println("\nNope... Box id is always a number, try again\n");

            } catch (Exception e) {
                System.out.println("Error occured: " + e);

            }
        }

    }

    public void updateBoxSymbol(BoardBox box, String symbol, int boxId, Player playerObj) {

        System.out.printf("\n%s chose box-%d to place %s\n\n", playerObj.getName(),
                boxId, symbol);

        box.setBoxSymbol(symbol, playerObj.getColor());

    }

    public void displayPlayersScore(Player[] players) {
        System.out.println("\nplayers score:\n");
        for (Player player : players) {
            String color = player.getColor();

            System.out.printf(
                    "Player: %s%s%s, score: %s%d%s\n",
                    color, player.getName(), TicToc.RESET,
                    color, player.getScore(), TicToc.RESET);
        }

    }

    public void updatePlayerScore(Player playerObj, int newScore) {

        playerObj.updateScore(newScore);

    }
}
