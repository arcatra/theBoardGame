package tictoc.helpers;

// Imports ------------------
import java.util.Scanner;

import tictoc.GameEntry;
import tictoc.utils.BoardBox;
import tictoc.utils.Player;
// -------------------------

public class GameLoopHelper {

    public final String readSymbol(Scanner input) {
        while (true) {
            System.out.print("\nEnter the chosen symbol: ");
            String symbol = input.nextLine().toUpperCase();

            if (!symbol.isBlank()) {
                if ("O".equals(symbol) || "X".equals(symbol) || "XOX".equals(symbol)) {
                    return symbol;

                }

            }

            System.out.println("\nOnly symbols 'x', 'o' or 'xox'(exit) are allowed, try again\n");

        }

    }

    public final int readBoxId(Scanner input, int boardSize) {
        int id;
        while (true) {
            System.out.print("Enter the choosen box ID: ");
            try {
                id = Integer.parseInt(input.nextLine());

                if (id <= (boardSize) && id > 0)
                    return id;

                System.out.printf("\nInvalid box id, should be <=%d and >=1, Please try again\n\n",
                        (boardSize));

            } catch (NumberFormatException e) {
                System.out.println("\nNope... Box id is always a number, Try again\n");

            } catch (Exception e) {
                System.out.println("Unknown error occured, Try again");

            }
        }

    }

    public final void updateBoxSymbol(BoardBox box, String newSymbol, int boxId, Player playerObj) {

        System.out.printf("\n%s chose box-%d to place %s\n\n", playerObj.getName(),
                boxId, newSymbol);

        box.setBoxSymbol(newSymbol, playerObj.getColor());

    }

    public final void displayPlayersScore(Player[] players) {
        System.out.println("\nplayers score:\n");
        for (Player player : players) {
            String color = player.getColor();

            System.out.printf(
                    "Player: %s%s%s, score: %s%d%s\n",
                    color, player.getName(), GameEntry.RESET,
                    color, player.getScore(), GameEntry.RESET);
        }

    }

    public final void updatePlayerScore(Player playerObj, int newScore) {

        playerObj.updateScore(newScore);

    }
}
