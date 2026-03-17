package tictoc;

// Import -----------
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import tictoc.helpers.*;
import tictoc.utils.*;

// -------------------
public class GameEntry {
    private int rows;
    private int columns;

    private Board board;
    public Map<Integer, int[]> boxMap = new HashMap<>();
    private Player[] players = new Player[2];

    final static public String RESET = "\u001B[0m";
    final static public String RED = "\u001B[31m";
    final static public String GREEN = "\u001B[32m";
    final static public String YELLOW = "";

    public GameEntry(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        this.board = new Board(this.rows, this.columns, this);

    }

    private final void GameRules() {
        System.out.println("\n");
        try (BufferedReader br = new BufferedReader(new FileReader("./src/rules.txt"))) {
            String line = br.readLine();
            while (line != null) {
                System.out.println(line);

                line = br.readLine();
            }

        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
            System.out.println("You have to be on 'tictoc' folder to get rules\n");

        } finally {
            System.out.printf("\nNote: The box's id starts from 1, and ends at %d\n\n", (this.board.boardSize));

        }
    }

    public void knowRules(Scanner input) {
        System.out.print("\nDo you know the rules of this game?(n: for rules) : ");
        try {
            String choice = input.nextLine().toLowerCase();
            if (choice.equals("n")) {
                this.GameRules();

            } else {
                System.out.println("\nOK, continue with the game :)\n");

            }

        } catch (InputMismatchException e) {
            System.out.println("Opps.., only 'y / n' is allowed\n");
        }
    }

    public void onboardPlayer(Scanner input) {

        System.out.println("\nRegister players:\n");
        int id = 0;
        String playerName;

        PlayerRegHelper playerRegHelper = new PlayerRegHelper();

        while (id < 2) {
            playerName = playerRegHelper.readPlayerName(input, id);

            if (!playerName.equals("")) {

                if (id > 0 && players[0].getName().equals(playerName)) {
                    System.out.println("This player name is same as the last one, use a different NAME\n");
                    continue;
                }

                String color = id > 0 ? GREEN : RED;
                players[id] = new Player(0, playerName, color);
                id++;

            } else {
                System.out.println("Invalid user name\n");

            }

        }

        System.out.println("\nDisplaying the Board one more time for you\n");

        this.board.displayBoard();
        playerRegHelper.displayInitialPlayersInfo(this.players, new String[] { RESET, RED, GREEN });

    }

    public void startGameLoop(Scanner input) {
        System.out.println(RED + "\nEnter xox as symbol to EXIT game" + RESET);
        GameLoopHelper gameLoopHelper = new GameLoopHelper();

        int boxesFilled = 0;
        boolean gameOver = false;
        Player currPlayerObj = this.players[0];

        while (!(gameOver)) {
            System.out.printf("\nTotal boxes filled so far: %d\n", boxesFilled);
            System.out.printf("Current player: %s\n", currPlayerObj.getName());

            String symbol = gameLoopHelper.readSymbol(input);

            if (symbol.equals("XOX")) {
                System.out.println("\nEXIT confirmed\n");
                break;
            }

            int boxId = gameLoopHelper.readBoxId(input, this.board.boardSize);

            int[] indices = this.boxMap.get(boxId);
            int rowIndex = indices[0];
            int columnIndex = indices[1];

            BoardBox box = this.board.getBoardBox(rowIndex, columnIndex);

            if (box.isBoxEmpty()) {
                PlayerScoreHelper scoreHelper = new PlayerScoreHelper(this.columns, this.board);

                gameLoopHelper.updateBoxSymbol(box, symbol, boxId, currPlayerObj);
                this.board.displayBoard();

                int totalScore = scoreHelper.getCurrPlayerPoints(rowIndex, columnIndex, box.getSymbol());
                if (totalScore > 0) {
                    System.out.printf("\n%s+%d for %s%s\n", currPlayerObj.getColor(), totalScore,
                            currPlayerObj.getName(), RESET);

                    gameLoopHelper.updatePlayerScore(currPlayerObj, totalScore);
                    gameLoopHelper.displayPlayersScore(this.players);

                }

                currPlayerObj = (currPlayerObj == this.players[0]) ? this.players[1] : this.players[0];
                boxesFilled++;

            } else {
                System.out.println("\nBox already filled, select a different box id\n");

            }

            if (boxesFilled == (this.board.boardSize)) {
                gameOver = true;
            }
        }

    }

    public void declareTheWinner() {

        var wonPlayer = this.getWonPlayer();

        if (wonPlayer == null) {
            System.out.println(GREEN + "\nThis game is a TIE, Play a new game again\n" + RESET);

            return;
        }

        var lostPlayer = this.getLostPlayer(wonPlayer);
        var score = (wonPlayer.getScore() - lostPlayer.getScore());

        System.out.printf(
                wonPlayer.getColor() + "\n%s WON the game against %s by %d point%s\n\n" + RESET,
                wonPlayer.getName(),
                lostPlayer.getName(),
                score,
                score > 1 ? "s" : "");

    }

    // Helper methods of DecalreTheWinner method ------------------
    private Player getWonPlayer() {

        if (players[0].getScore() == players[1].getScore()) {
            return null;
        }

        return (players[0].getScore() > players[1].getScore()) ? players[0] : players[1];
    }

    private Player getLostPlayer(Player wonPlayer) {

        return (wonPlayer == players[0] ? players[1] : players[0]);

    }

    // helper methods end ----------------------------------------

    private static boolean isValidArgs(String[] args) {
        System.out.println(RESET);

        if (args.length == 2 && (args[0].equals(args[1])) && (Integer.parseInt(args[0]) <= 8)) {
            System.out.println("Done, rows & columns are set\n");
            return true;

        }

        System.out.println(GameEntry.RESET);
        System.out.println(
                "additional args: cannot find any(java -cp ... <rows columns>) or may be Invalid");
        System.out.println("rows = columns & rows, columns <= 8\n");
        System.out.println("\nDefaulting the board size to 4 x 4.\n\n");

        return false;
    }

    public static void main(String[] args) {
        int argRows = 4;
        int argColumns = 4;

        // System.out.println(
        // GameEntry.RED + "\nMake sure you're calling from the right folder: tictoc" +
        // GameEntry.RESET);

        if (GameEntry.isValidArgs(args)) {
            argRows = Integer.parseInt(args[0]);
            argColumns = Integer.parseInt(args[1]);

        }

        Scanner userIn = new Scanner(System.in);
        GameEntry gameObj = new GameEntry(argRows, argColumns);

        gameObj.board.buildBoard();
        gameObj.board.displayBoard();

        gameObj.knowRules(userIn);
        gameObj.onboardPlayer(userIn);
        gameObj.startGameLoop(userIn);
        gameObj.declareTheWinner();

        userIn.close();
    }
}

// ------------------------------------------------
