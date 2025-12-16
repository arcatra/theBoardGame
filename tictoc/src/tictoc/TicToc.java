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

    private Player[] players = {
            new Player(0, "", ""),
            new Player(0, "", "")
    };
    private Map<String, Integer> playersMap = new HashMap<>();

    private ArrayList<String> alreadyMatched = new ArrayList<>(); // store group indexes, already matched string
                                                                  // indices, in
    // list form

    final String RESET = "\u001B[0m";
    final String RED = "\u001B[31m";
    final String GREEN = "\u001B[32m";

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
                boxMap.put(boxId, new int[] { row, column });

                boxId++;
            }

            board.add(boxes);
        }

        System.out.printf(
                "Success, constructed board with %d rows and %d columns\n\n", this.rows, this.columns);
    }

    public void displayBoard() {
        String boxString = "|  0  |";
        int repeatCount = boxString.length() - 2;

        for (BoardBox[] rowValues : board) {
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

        System.out.println(RED + "Red = " + players[0] + RESET);
        System.out.println(GREEN + "Green = " + players[1] + RESET);
    }

    public void registerPlayers(Scanner input) {
        System.out.println("\nPlayers details: Register\n");
        int id = 0;
        String userName;
        while (id < 2) {
            System.out.printf("Name of Player-%d: ", id);
            try {
                userName = input.nextLine();
                if (!userName.equals("") && !(players[id == 1 ? 0 : id]).playerName.equals(userName)) {
                    Player player = players[id];

                    player.playerName = userName;
                    player.defaultColor = id > 0 ? GREEN : RED;

                    playersMap.put(userName, player.score);
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

        Player currentPlayerObj = players[0];

        int filledBoxes = 0;
        boolean stop = false;

        while (!(stop)) {

            if (filledBoxes == (this.rows * this.columns)) {
                System.out.println("\nPlayers scores: " + playersMap);
                break;
            }

            System.out.println("Current player: " + currentPlayerObj.playerName);
            System.out.print("\nEnter choosen Symbol: ");
            String symbol = input.nextLine().toUpperCase();

            if (symbol.equals("XOX")) {
                System.out.println("\nPlayers scores");
                this.displayPlayerScores();
                break;
            }

            if (symbol.equals("X") || symbol.equals("O")) {

                if (lookAndCheckBox(input, symbol, currentPlayerObj)) {

                    currentPlayerObj = (currentPlayerObj == players[0]) ? players[1] : players[0];

                    filledBoxes++;
                }

                System.out.println("\nUpdated players scores\n");
                this.displayPlayerScores();

            } else {
                System.out.println("\nOnly symbols 'x' or 'o' are allowed, try again\n");
            }

        }
    }

    private boolean checkForBreakCon() {
        return false;
    }

    private void displayPlayerScores() {
        for (Player player : this.players) {
            System.out.printf("Player: %s, score: %d", player.playerName, player.score);
        }
    }

    private boolean lookAndCheckBox(Scanner input, String symbol, Player currentPlayer) {
        String currPlayerName = currentPlayer.playerName;

        System.out.print("Enter the choosen box ID: ");
        try {
            // --------------------------
            int boxId = input.nextInt();
            input.nextLine(); // reads dead line
            // --------------------------

            if (boxId <= (this.rows * this.columns) && boxId > 0) {

                int[] indices = boxMap.get(boxId);

                int rowIndex = indices[0];
                int columnIndex = indices[1];
                BoardBox box = board.get(rowIndex)[columnIndex];

                if ((!box.symbol.equals("X") && !box.symbol.equals("O"))) {
                    this.updateBox(box, symbol, boxId, currentPlayer);
                    this.displayBoardWithInfo();

                    int playerScore = this.lookForPoints(rowIndex, columnIndex, box.symbol);
                    playersMap.put(currPlayerName, playersMap.get(currPlayerName) + playerScore);

                    System.out.printf("\n%sTotal Points: %d %s\n", box.symColor, playerScore, RESET);

                    return true;

                } else {
                    System.out.println("\nBox already filled, select a different box id\n");
                }

            } else {
                System.out.println("\nInvalid box id, Please try again\n");

            }

        } catch (InputMismatchException e) {
            System.out.println("\nNope... Box id is always a number, try again\n");
            input.nextLine();

        } catch (Exception e) {
            System.out.println("Error occured: " + e);
            input.nextLine();
        }

        return false;
    }

    private void updateBox(BoardBox box, String symbol, int boxId, Player currentPlayer) {

        System.out.printf("\n%s choosed box-%d to place %s\n\n", currentPlayer.playerName,
                boxId, symbol);

        box.symbol = symbol;
        box.symColor = currentPlayer.defaultColor;

    }

    public int lookForPoints(int row, int column, String boxSymbol) {
        int points = 0;

        if (boxSymbol.equals("X")) {
            // for horizontal
            points += this.getValidStringPoints(row, column, 2, false, "XOX");

            // for vertical
            points += this.getValidStringPoints(column, row, 2, true, "XOX");

        } else if (boxSymbol.equals("O")) {

            int tempColumn = Math.max(0, column - 1);
            points += this.getValidStringPoints(row, tempColumn, 2, false, "XOX");

            int tempRow = Math.max(0, row - 1);
            points += this.getValidStringPoints(column, tempRow, 2, true, "XOX");

        }

        return points;
    }

    public int getValidStringPoints(int row, int column, int beyond, boolean swap, String match) {
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
                    oneCycleString.append(board.get(index)[row].symbol);

                } else {
                    oneCycleStringIndexes.append(row).append(index);
                    oneCycleString.append(board.get(row)[index].symbol);

                }

                if (oneCycleString.toString().equals(match)) {
                    if (!alreadyMatched.contains(oneCycleStringIndexes.toString())) {
                        validStringsCount++;
                        alreadyMatched.add(oneCycleStringIndexes.toString());

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
                oneCycleString.append(board.get(index)[row].symbol);

            } else {
                oneCycleStringIndexes.append(row).append(index);
                oneCycleString.append(board.get(row)[index].symbol);

                // System.err.println("Cycle string: " + oneCycleString);
                // System.err.println("Cycle index string: " + oneCycleStringIndexes);

            }

            String finalString = oneCycleString.toString();
            String finalIndex = oneCycleStringIndexes.toString();

            if (finalString.equals(match)) {
                if (!alreadyMatched.contains(finalIndex)) {
                    validStringsCount++;
                    alreadyMatched.add(finalIndex);

                }
            }
        }

        return validStringsCount;
    }

    public static void main(String[] args) {
        int argRows = 4;
        int argColumns = 4;

        if (args.length == 2) {
            argRows = Integer.parseInt(args[0]);
            argColumns = Integer.parseInt(args[1]);

        } else {
            System.out.println("\nDefaulting the board size to 4 x 4.\n");
        }

        Scanner userIn = new Scanner(System.in);
        TicToc xox = new TicToc(argRows, argColumns);

        xox.buildBoard();
        xox.displayBoard();
        xox.knowRules(userIn);
        xox.registerPlayers(userIn);
        xox.startGameLoop(userIn);

        userIn.close();
    }
}

// ------------------------------------------------
