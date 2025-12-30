package tictoc.utils;

public class Player {
    private int score;
    private String name = "";
    private String defaultColor;

    public Player(int score, String playerName, String color) {
        this.score = score;
        this.name = playerName;
        this.defaultColor = color;
    }

    public int getScore() {
        return this.score;

    }

    public String getName() {
        return this.name;

    }

    public String getColor() {
        return this.defaultColor;

    }

    public void updateScore(int newScore) {
        this.score += newScore;

    }
}
