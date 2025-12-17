package tictoc;

public class Player {
    int score;
    String name = "";
    String defaultColor;

    public Player(int score, String playerName, String color) {
        this.score = score;
        this.name = playerName;
        this.defaultColor = color;
    }

}
