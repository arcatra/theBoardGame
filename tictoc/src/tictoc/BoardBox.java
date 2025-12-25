package tictoc;

public class BoardBox {
    private String symbol;
    private String symColor;
    private int id;

    public BoardBox(String symbol, String symColor) {
        this.symbol = symbol;
        this.symColor = symColor;
        this.id = Integer.parseInt(symbol);

    }

    public boolean isBoxEmpty() {
        return (!this.symbol.equals("X") || !this.symbol.equals("O"));

    }

    public void setBoxSymbol(String newSymbol, String newSymColor) {
        this.symbol = newSymbol;
        this.symColor = newSymColor;

    }

    public String getSymbol() {
        return this.symbol;

    }

    public String getSymbolColor() {
        return this.symColor;

    }

    public int getBoxId() {
        return this.id;
    }

}