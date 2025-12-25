package tictoc;

// Import -----------
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
// import java.lang.StringBuilder;
// ------------------

public class TicToc {
    int rows;
    int columns;

    private ArrayList<BoardBox[]> board = new ArrayList<>();
    private Map<Integer, int[]> boxMap = new HashMap<>();

    public Player[] players = new Player[2];

    private ArrayList<String> alreadyMatched = new ArrayList<>();
    // store already matched string indices, in list form

    final static String RESET = "\u001B[0m";
    final static String RED = "\u001B[31m";
    final static String GREEN = "\u001B[32m";

    public TicToc(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    public void GameRules() {
        System.out.println("\nRules -------------------------------------------------------------\n");
        System.out.println("- This is a two player game,");
        System.out.println("- This game consists of two symbols 'x' and 'o'.");
        System.out.println("- On every turn each player will choose any one of the sym -");
        System.out.println("- to place it inside any one of the boxes on the board.");
        System.out.println("- Example: x, in next line: 12");
        System.out.println("Where:");
        System.out.println("\t- 'x' : The choosen symbol.");
        System.out.println("\t- '12' : The choosen boxID.\n");
        System.out.println("- Only rows, columns will be considerd for points, no diagonals.\n");
        System.out.printf("Note: The box's id starts from 1, and ends at %d\n", (this.rows * this.columns));
        System.out.println("\n- Finally player with more points or score will win the game!");
        System.out.println("\nRules end ----------------------------------------------------------\n");
    }

    public void knowRules(Scanner input) {
        System.out.print("\nDo you know the rules of this game?(y/n) : ");
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

    public void buildBoard() {
        System.out.println("Trying to build the board");
        int boxId = 1;
        for (int row = 0; row < this.rows; row++) {
            BoardBox[] boxes = new BoardBox[this.columns];

            for (int column = 0; column < this.columns; column++) {
                boxes[column] = new BoardBox(Integer.toString(boxId), RESET);
                this.boxMap.put(boxId, new int[] { row, column });

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

                boxString = (boxSymbol.length() < 2 ? "|  " : "| ") + symbolColor + boxSymbol + RESET
                        + "  ";

                System.out.print(boxString);
            }
            System.out.println("|");
        }
        System.out.println(("+" + "-".repeat(repeatCount)).repeat(this.columns) + "+");

    }

    private void displayBoardWithInfo() {

        this.displayBoard();

        System.out.printf("%sRed%s = %s\n", RED, RESET, this.players[0].getName());
        System.out.printf("%sGreen%s = %s\n\n", GREEN, RESET, this.players[1].getName());

    }

    public void onboardPlayer(Scanner input) {
        System.out.println("\nRegister player details:\n");
        int id = 0;
        String userName;
        while (id < 2) {
            System.out.printf("Name of Player-%d: ", id + 1);
            try {
                userName = input.nextLine().toUpperCase();
                if (!userName.equals("")) {

                    if (id > 0 && players[0].getName().equals(userName)) {
                        System.out.println("This player name is same as the last one, use a different NAME\n");
                        continue;
                    }

                    String color = id > 0 ? GREEN : RED;

                    players[id] = new Player(0, userName, color);
                    id++;

                } else {
                    System.out.println("Invalid user name\n");

                }

            } catch (InputMismatchException e) {
                System.out.println("\nPlayer name can only be a String, try again\n");

            } catch (Exception e) {
                System.out.println("Currently handling players registration");
                System.out.println("Opps.. error occured: " + e.getLocalizedMessage());
            }
        }

        System.out.println("\nDisplaying the Board one more time for you\n");
        this.displayBoardWithInfo();

    }

    public void startGameLoop(Scanner input) {
        System.out.println(RED + "\nEnter xox as symbol to EXIT game" + RESET);

        int filledBoxes = 0;
        boolean gameOver = false;
        Player currPlayerObj = this.players[0];

        while (!(gameOver)) {
            System.out.printf("\nTotal boxes filled so far: %d\n", filledBoxes);
            System.out.printf("Current player: %s\n", currPlayerObj.getName());

            String symbol = this.readSymbol(input);

            if (symbol.equals("XOX")) {
                System.out.println("\nEXIT confirmed\n");
                break;
            }

            int boxId = this.readBoxId(input);

            int[] indices = this.boxMap.get(boxId);
            int rowIndex = indices[0];
            int columnIndex = indices[1];

            BoardBox box = this.board.get(rowIndex)[columnIndex];

            if (box.isBoxEmpty()) {
                this.updateBox(box, symbol, boxId, currPlayerObj);

                this.displayBoard();

                int points = this.getCurrPlayerPoints(rowIndex, columnIndex, box.getSymbol());
                if (points > 0) {
                    System.out.printf("\n+%d for %s\n", points, currPlayerObj.getName());

                    currPlayerObj.updateScore(points);
                    this.displayPlayersScore();
                }

                currPlayerObj = (currPlayerObj == this.players[0]) ? this.players[1] : this.players[0];

                filledBoxes++;

            } else {
                System.out.println("\nBox already filled, select a different box id\n");

            }

            if (filledBoxes == (this.rows * this.columns)) {
                gameOver = true;
            }
        }

    }

    // helper methods of GameLoop -----------------------------------

    private String readSymbol(Scanner input) {
        while (true) {
            System.out.print("\nEnter the choosen symbol: ");
            String symbol = input.nextLine().toUpperCase();

            if ("XO".contains(symbol) || "XOX".equals(symbol)) {
                return symbol;

            } else {
                System.out.println("\nOnly symbols 'x', 'o' or 'xox'(exit) are allowed, try again\n");

            }
        }
    }

    private int readBoxId(Scanner input) {
        int id;
        while (true) {
            System.out.print("Enter the choosen box ID: ");
            try {
                id = Integer.parseInt(input.nextLine());

                if (id <= (this.rows * this.columns) && id > 0)
                    return id;

                System.out.printf("\nInvalid box id, should be <=%d and >=1, Please try again\n\n",
                        (this.rows * this.columns));

            } catch (NumberFormatException e) {
                System.out.println("\nNope... Box id is always a number, try again\n");

            } catch (Exception e) {
                System.out.println("Error occured: " + e);

            }
        }

    }

    private void updateBox(BoardBox box, String symbol, int boxId, Player currentPlayer) {

        System.out.printf("\n%s choosed box-%d to place %s\n\n", currentPlayer.getName(),
                boxId, symbol);

        box.setBoxSymbol(symbol, currentPlayer.getColor());

    }

    private void displayPlayersScore() {
        System.out.println("\nplayers score:\n");
        for (Player player : this.players) {
            String color = player.getColor();

            System.out.printf(
                    "Player: %s%s%s, score: %s%d%s\n",
                    color, player.getName(), RESET,
                    color, player.getScore(), RESET);
        }

    }

    // helper methods end ---------------------------------------------------------

    private int getCurrPlayerPoints(int row, int column, String boxSymbol) {
        int points = 0;

        if (boxSymbol.equals("X")) {
            // for horizontal
            points += this.getValidPoints(row, column, 2, false, "XOX");

            // for vertical
            points += this.getValidPoints(column, row, 2, true, "XOX");

        } else if (boxSymbol.equals("O")) {

            points += this.getValidPoints(row, column, 1, false, "XOX");

            points += this.getValidPoints(column, row, 1, true, "XOX");

        }

        return points;
    }

    private int getValidPoints(int row, int column, int beyond, boolean swap, String match) {
        int validPoints = 0;

        int start = Math.max(0, column - beyond);
        int end = Math.min(column + beyond, (this.columns - 1));

        return validPoints;
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

        if (args.length == 2) {
            argRows = Integer.parseInt(args[0]);
            argColumns = Integer.parseInt(args[1]);

        } else {
            System.out.println(TicToc.RESET);
            System.out.println("\nDefaulting the board size to 4 x 4.\n");
        }

        Scanner userIn = new Scanner(System.in);
        TicToc xox = new TicToc(argRows, argColumns);

        xox.buildBoard();
        xox.displayBoard();
        xox.knowRules(userIn);
        xox.onboardPlayer(userIn);
        xox.startGameLoop(userIn);

        xox.declareTheWinner();

        userIn.close();

    }
}

// ------------------------------------------------
