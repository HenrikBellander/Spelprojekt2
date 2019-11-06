import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    static Random r = new Random();

    public static void main(String[] args) throws IOException, InterruptedException {

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();      //Skapar terminalfönster
        Terminal terminal = terminalFactory.createTerminal();

        Player player = new Player ();                                              //Skapar spelare
        terminal.setCursorVisible(false);
        terminal.setForegroundColor(TextColor.ANSI.WHITE);
        Obstacle o1 = new Obstacle(buildWall());


        do {
            terminal.setCursorPosition(player.getX(), player.getY());
            terminal.putCharacter(player.getPlayerChar());

            terminal.flush();



            int counter=0;
            KeyStroke keyStroke = null;
            do {
                Thread.sleep(5); // might throw InterruptedException
                keyStroke = terminal.pollInput();
                counter++;


                if (counter == 50 || keyStroke != null) {
                    terminal.clearScreen();
                    terminal.setCursorPosition(player.getX(), player.getY());
                    terminal.putCharacter(player.getPlayerChar());
                    terminal.flush();
                    for (Position p : o1.obstacleList) {
                        if (p.getX() >= 0) {
                            terminal.setCursorPosition(p.getX(), p.getY());
                            terminal.putCharacter('X');
                            p.setX(p.getX() - 1);
                            terminal.flush();
                        } else {
                            o1.obstacleList = buildWall();
                            break;
                        }
                    }
                    counter = 0;
                }

            } while (keyStroke == null);

            terminal.setCursorPosition(player.getX(), player.getY());
            terminal.putCharacter(' ');

            KeyType type = keyStroke.getKeyType();

            switch (type) {                                     //Vår förflyttning
                case ArrowDown:
                    if (player.getY() == 23) {
                    } else {
                        player.setY(player.getY() + 1);
                    }
                    break;
                case ArrowUp:
                    if (player.getY() == 0) {
                    } else {
                        player.setY(player.getY() -1);
                    }
                    break;
            }
            terminal.flush();

        } while (true);

    }

    private static List<Position> buildWall() {
        int y = 0;
        int rand = r.nextInt(14);
        List<Position> walls = new ArrayList<>();                   //Placerar walls
        for (int i = 0; i < 24; i++) {
            if (i < rand || i >= rand+10){
                walls.add(new Position(79, y));
            }
            y++;
        }
        return walls;
    }
}

