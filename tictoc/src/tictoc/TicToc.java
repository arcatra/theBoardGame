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
        System.out.println("- to place it inside any of the boxes on the board.");
        System.out.println("- Example: x, in next line: 12");
        System.out.println("Where:");
        System.out.println("\t- 'x' means the symbol that player have choosen.");
        System.out.println("\t- '12' means the box id that this symbol(x) will be placed in.\n");
        System.out.println("- Only rows, columns, no diagonals will be considerd for points.\n");
        System.out.println("Note: The box id's starts with 1");
        System.out.println("\n- Finally player with more points/ score wins the game!");
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
                boxString = (box.symbol.length() < 2 ? "|  " : "| ") + box.symColor + box.symbol + RESET
                        + "  ";

                System.out.print(boxString);
            }
            System.out.println("|");
        }
        System.out.println(("+" + "-".repeat(repeatCount)).repeat(this.columns) + "+");

    }

    private void displayBoardWithInfo() {

        this.displayBoard();

        System.out.println(RED + "Red = " + this.players[0].name + RESET);
        System.out.println(GREEN + "Green = " + this.players[1].name + RESET);
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

                    if (id > 0 && players[id - 1].name.equals(userName)) {
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

            }
        }

        System.out.println("\nDisplaying the Board one more time for you\n");
        this.displayBoardWithInfo();

    }

    public void startGameLoop(Scanner input) {
        System.out.println(RED + "\nEnter xox as symbol to EXIT game" + RESET);

        int filledBoxes = 0;
        boolean stop = false;
        Player currentPlayerObj = this.players[0];

        while (!(stop)) {
            System.out.printf("\nTotal boxes filled so far: %d\n", filledBoxes);
            System.out.printf("Current player: %s\n", currentPlayerObj.name);

            String symbol = this.getUserSelectedSymbol(input);

            if (symbol.length() == 1 && "XO".contains(symbol)) {
                int boxId = this.getUserSelectedBoxId(input);

                if (boxId <= (this.rows * this.columns) && boxId > 0) {
                    int[] indices = this.boxMap.get(boxId);
                    int rowIndex = indices[0];
                    int columnIndex = indices[1];

                    BoardBox box = this.board.get(rowIndex)[columnIndex];

                    if (this.isBoxAvailable(box)) {
                        this.updateBox(box, symbol, boxId, currentPlayerObj);
                        this.displayBoard();

                        int points = this.getCurrPlayerPoints(rowIndex, columnIndex, box.symbol);
                        this.updatePlayerPoints(points, currentPlayerObj);

                        currentPlayerObj = (currentPlayerObj == this.players[0]) ? this.players[1] : this.players[0];

                        this.displayPlayersScore();

                        filledBoxes++;

                    } else {
                        System.out.println("\nBox already filled, select a different box id\n");

                    }

                } else {
                    System.out.printf("\nInvalid box id, should be <=%d and >=1, Please try again\n\n",
                            (this.rows * this.columns));

                }

            } else {
                if (!symbol.equals("XOX")) {
                    System.out.println("\nOnly symbols 'x' or 'o' are allowed, try again\n");

                } else {
                    System.out.println("\nExit confirmed\n");

                }
            }

            if (this.checkForBreakCondition(filledBoxes, symbol)) {
                stop = true;
            }

        }
    }

    // helper methods of GameLoop -----------------------------------
    private boolean checkForBreakCondition(int boxes, String symbol) {

        return (boxes == (this.rows * this.columns)) || symbol.equals("XOX");

    }

    private String getUserSelectedSymbol(Scanner input) {
        System.out.print("\nEnter choosen Symbol: ");
        String pName = input.nextLine().toUpperCase();

        return pName;
    }

    private int getUserSelectedBoxId(Scanner input) {
        int boxId = 0;

        System.out.print("Enter the choosen box ID: ");
        try {
            boxId = Integer.parseInt(input.nextLine());

        } catch (NumberFormatException e) {
            System.out.println("\nNope... Box id is always a number, try again\n");

        } catch (Exception e) {
            System.out.println("Error occured: " + e);

        }

        return boxId;
    }

    private boolean isBoxAvailable(BoardBox box) {

        return (!box.symbol.equals("X") && !box.symbol.equals("O"));

    }

    private void updateBox(BoardBox box, String symbol, int boxId, Player currentPlayer) {

        System.out.printf("\n%s choosed box-%d to place %s\n\n", currentPlayer.name,
                boxId, symbol);

        box.symbol = symbol;
        box.symColor = currentPlayer.defaultColor;

    }

    private void updatePlayerPoints(int points, Player player) {
        if (points > 0) {
            player.score += points;
        }

    }

    private void displayPlayersScore() {
        System.out.println("\nplayers score:\n");
        for (Player player : this.players) {
            String color = player.defaultColor;

            System.out.printf(
                    "Player: %s%s%s, score: %s%d%s\n",
                    color, player.name, RESET,
                    color, player.score, RESET);
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

            int tempColumn = Math.max(0, column - 1);
            points += this.getValidPoints(row, tempColumn, 2, false, "XOX");

            int tempRow = Math.max(0, row - 1);
            points += this.getValidPoints(column, tempRow, 2, true, "XOX");

        }

        return points;
    }

    private int getValidPoints(int row, int column, int beyond, boolean swap, String match) {
        int validStringsCount = 0;

        int start = Math.max(0, column - beyond);
        int end = Math.min(column + beyond, (this.columns - 1));

        StringBuilder oneCycleStringIndexes = new StringBuilder();
        StringBuilder oneCycleString = new StringBuilder();

        // System.out.printf(" start, end : [%d, %d]\n", start, end);

        for (int index = start; index <= end; index++) {

            if (index == column) {
                if (swap) {
                    oneCycleStringIndexes.append(index).append(row);
                    oneCycleString.append(this.board.get(index)[row].symbol);

                } else {
                    oneCycleStringIndexes.append(row).append(index);
                    oneCycleString.append(this.board.get(row)[index].symbol);

                }

                if (oneCycleString.toString().equals(match)) {
                    if (!this.alreadyMatched.contains(oneCycleStringIndexes.toString())) {
                        validStringsCount++;
                        this.alreadyMatched.add(oneCycleStringIndexes.toString());

                    }
                }

                // System.err.println("Cycle string before reset: " + oneCycleString);
                // System.err.println("Cycle index string before reset: " +
                // oneCycleStringIndexes);

                oneCycleStringIndexes.setLength(0); // Reset
                oneCycleString.setLength(0); // Reset

            }

            if (swap) {
                // System.out.println("Swapping row and column");
                oneCycleStringIndexes.append(index).append(row);
                oneCycleString.append(this.board.get(index)[row].symbol);

            } else {
                oneCycleStringIndexes.append(row).append(index);
                oneCycleString.append(this.board.get(row)[index].symbol);

                // System.err.println("Cycle string: " + oneCycleString);
                // System.err.println("Cycle index string: " + oneCycleStringIndexes);

            }

            String finalString = oneCycleString.toString();
            String finalIndex = oneCycleStringIndexes.toString();

            if (finalString.equals(match)) {
                if (!this.alreadyMatched.contains(finalIndex)) {
                    validStringsCount++;
                    this.alreadyMatched.add(finalIndex);

                }
            }
        }

        return validStringsCount;
    }

    public void declareTheWinner() {

        Player wonPlayer = this.getWonPlayer();

        if (wonPlayer == null) {
            System.out.println(GREEN + "\nThis game is a TIE, Play a new game again\n" + RESET);

            return;
        }

        Player lostPlayer = this.getLostPlayer(wonPlayer);

        System.out.printf(
                wonPlayer.defaultColor + "\n\n%s WON the game against %s by %d points\n\n" + RESET,
                wonPlayer.name,
                lostPlayer.name,
                (wonPlayer.score - lostPlayer.score));

    }

    // Helper methods of DecalreTheWinner method ------------------
    private Player getWonPlayer() {

        if (players[0].score == players[1].score) {
            return null;
        }

        return (players[0].score > players[1].score) ? players[0] : players[1];
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
