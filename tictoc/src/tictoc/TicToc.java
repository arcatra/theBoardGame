package tictoc;

// Import -----------
// import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

import tictoc.helpers.GameLoopHelper;
import tictoc.helpers.PlayerScoreHelper;
import tictoc.utils.Board;
import tictoc.utils.BoardBox;
import tictoc.utils.Player;
// ------------------

public class TicToc {
    private int rows;
    private int columns;

    private Board board;
    public Map<Integer, int[]> boxMap = new HashMap<>();

    private Player[] players = new Player[2];

    // private ArrayList<String> alreadyMatched = new ArrayList<>();
    // store already matched string indices, in list form

    final static public String RESET = "\u001B[0m";
    final static String RED = "\u001B[31m";
    final static String GREEN = "\u001B[32m";
    final static String YELLOW = "";

    public TicToc(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;

        this.board = new Board(rows, columns, this);

    }

    public void GameRules() {
        System.out.println("\nRules -------------------------------------------------------------\n");
        System.out.println("- This is a two player game,");
        System.out.println("- This game consists of two symbols: 'x' & 'o'.");
        System.out.println("- On every turn each player will choose any one of the sym -");
        System.out.println("- to place it inside any one of the boxes on the board.");
        System.out.println("- Example: x <Enter>, in next line: 12 <Enter>");
        System.out.println("Where:");
        System.out.println("\t- 'x' : The choosen symbol.");
        System.out.println("\t- '12' : The choosen boxID.\n");
        System.out.println("- For every xox match, appropriate player will get a point");
        System.out.println("- Only rows, columns will be considerd for points, no diagonals.\n");
        System.out.printf("Note: The box's id starts from 1, and ends at %d\n", (this.rows * this.columns));
        System.out.println("\n- Finally player with more points or score will win the game!");
        System.out.println("\nRules end ----------------------------------------------------------\n");
    }

    public void knowRules(Scanner input) {
        System.out.print("\nDo you know the rules of this game?(n-for rules) : ");
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

        while (id < 2) {
            System.out.printf("Name of Player-%d: ", id + 1);
            playerName = this.readPlayerName(input);
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
        this.displayInitialPlayersInfo();

    }

    private final void displayInitialPlayersInfo() {

        System.out.printf("%sRed%s = %s\n", RED, RESET, this.players[0].getName());
        System.out.printf("%sGreen%s = %s\n\n", GREEN, RESET, this.players[1].getName());

    }

    private String readPlayerName(Scanner input) {
        String playerName = "";

        try {
            playerName = input.nextLine().toUpperCase();

        } catch (InputMismatchException e) {
            System.out.println("\nPlayer name can only be a String, try again\n");

        } catch (Exception e) {
            System.out.println("Currently handling players registration");
            System.out.println("and.. error occured: " + e.getLocalizedMessage());

        }

        return playerName;
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

            int boxId = gameLoopHelper.readBoxId(input, this.rows, this.columns);

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
                    System.out.printf("\n+%d for %s\n", totalScore, currPlayerObj.getName());

                    gameLoopHelper.updatePlayerScore(currPlayerObj, totalScore);
                    gameLoopHelper.displayPlayersScore(this.players);

                }

                currPlayerObj = (currPlayerObj == this.players[0]) ? this.players[1] : this.players[0];
                boxesFilled++;

            } else {
                System.out.println("\nBox already filled, select a different box id\n");

            }

            if (boxesFilled == (this.rows * this.columns)) {
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

        System.out.printf(
                wonPlayer.getColor() + "\n%s WON the game against %s by %d points\n\n" + RESET,
                wonPlayer.getName(),
                lostPlayer.getName(),
                (wonPlayer.getScore() - lostPlayer.getScore()));

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

    public static void main(String[] args) {
        int argRows = 4;
        int argColumns = 4;

        if (args.length == 2 && (args[0].equals(args[1]))) {
            argRows = Integer.parseInt(args[0]);
            argColumns = Integer.parseInt(args[1]);

            System.out.println("Done, rows & columns are set\n");

        } else {
            System.out.println(TicToc.RESET);
            System.out.println("additional arguments: cannot find any(rows, columns : rows = columns) or Invalid");
            System.out.println("\nDefaulting the board size to 4 x 4.\n");
        }

        Scanner userIn = new Scanner(System.in);
        TicToc gameObj = new TicToc(argRows, argColumns);

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
