public class Player {
    private final int x = 10;
    private int y = 12;
    private final char playerChar = '\u265e';


    public char getPlayerChar() {
        return playerChar;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}