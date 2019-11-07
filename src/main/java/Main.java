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
    static int speed = 20;

    public static void main(String[] args) throws IOException, InterruptedException {

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();      //Skapar terminalfönster
        Terminal terminal = terminalFactory.createTerminal();
        terminal.setCursorVisible(false);
        terminal.setForegroundColor(TextColor.ANSI.WHITE);

        Player player = new Player ();                                              //Skapar spelare och väggar
        int lives = 3;
        List<Obstacle> walls = new ArrayList<>();
        walls.add(new Obstacle(buildWall(79)));
        walls.add(new Obstacle(buildWall(95)));
        walls.add(new Obstacle(buildWall(111)));
        walls.add(new Obstacle(buildWall(127)));
        walls.add(new Obstacle(buildWall(143)));



        int counter=0;                                  //Counter bestämmer hur ofta väggar flyttar sig, i princip dess hastighet. Ökar varje 5ms loop och nollställs efter movement.
        do {                                                                        //Gameloopen
            terminal.setCursorPosition(player.getX(), player.getY());               //Flytta spelare
            terminal.putCharacter(player.getPlayerChar());
            terminal.flush();


            KeyStroke keyStroke = null;
            do {
                Thread.sleep(5); // might throw InterruptedException
                keyStroke = terminal.pollInput();
                counter++;
                if (counter % 2000 == 0){           //Test att få det att gå snabbare
                    if (speed > 5) {
                        speed--;
                    }
                }
                if (counter % speed == 0 || keyStroke != null) {
                    terminal.clearScreen();
                    terminal.setCursorPosition(player.getX(), player.getY());               //Printar även ut spelare när väggar flyttar
                    terminal.putCharacter(player.getPlayerChar());
                    terminal.flush();
                    //TODO inte printa om x > 79

                    for (Obstacle o : walls) {                                              //Printar väggar
                        printWall(terminal, o);
                    }


                    for (Obstacle ob : walls) {                                             //Kollisionscheck med väggarna
                        for (Position p : ob.obstacleList) {
                            if (p.getX() == player.getX() && p.getY() == player.getY()) {
                                System.out.println("DEATH");    //TODO nåt bättre än detta
                                lives--;
                            }
                        }
                    }
                }
                if (lives == 0){                    //Kollar ofta
                    break;
                }
            } while (keyStroke == null);

            if (lives == 0){                        //Bryter gameloopen
                terminal.clearScreen();
                terminal.setCursorPosition(35, 12);
                terminal.setForegroundColor(TextColor.ANSI.RED);
                terminal.bell();
                String death = "YOU DIED!";
                for (int i = 0; i < death.length(); i++) {
                    terminal.putCharacter(death.charAt(i));
                }
                terminal.flush();
                break;
            }
            terminal.setCursorPosition(player.getX(), player.getY());                           //Suddar spelare efter knapptryck
            terminal.putCharacter(' ');

            KeyType type = keyStroke.getKeyType();
            switch (type) {                                     //Spelares förflyttning
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

        } while (true);                                                 //Gameloop slutar här
    }

    private static List<Position> buildWall(int startX) {
        int y = 0;
        int rand = r.nextInt(14);
        List<Position> walls = new ArrayList<>();                   //Initierar samt bygger nya walls efter att de nått slutet av sin resa
        for (int i = 0; i < 24; i++) {
            if (i < rand || i >= rand+10){
                walls.add(new Position(startX, y));
            }
            y++;
        }
        return walls;
    }

    private static void printWall(Terminal terminal, Obstacle o) throws IOException {               //Printar väggar
        for (Position p : o.obstacleList) {
            if (p.getX() >= 0) {
                terminal.setCursorPosition(p.getX(), p.getY());
                terminal.putCharacter('X');
                p.setX(p.getX() - 1);
                terminal.flush();
            } else {
                o.obstacleList = buildWall(79);
                break;
            }
        }
    }


}

