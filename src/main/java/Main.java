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


        Player player = new Player ();                                              //Skapar spelare och väggar
        int lives = 3;
        List<Obstacle> walls = new ArrayList<>();

        walls.add(new Obstacle(buildComet(79)));
        walls.add(new Obstacle(buildComet(95)));
        walls.add(new Obstacle(buildComet(111)));
        walls.add(new Obstacle(buildComet(127)));
        walls.add(new Obstacle(buildComet(143)));


        int counter=0;                                  //Counter bestämmer hur ofta väggar flyttar sig, i princip dess hastighet. Ökar varje 5ms loop
        do {                                                                        //Gameloopen
            terminal.setForegroundColor(TextColor.ANSI.MAGENTA);
            terminal.setCursorPosition(player.getX(), player.getY());               //Flytta spelare
            terminal.putCharacter(player.getPlayerChar());
//            terminal.setForegroundColor(TextColor.ANSI.WHITE);
            terminal.flush();

            KeyStroke keyStroke = null;
            do {
                Thread.sleep(5); // might throw InterruptedException
                keyStroke = terminal.pollInput();
                counter++;
                if (counter % 500 == 0){           //Test att få det att gå snabbare
                    if (speed > 5) {
                        speed--;
                    }
                }
                if (counter % speed == 0 || keyStroke != null) {
                    terminal.clearScreen();
                    terminal.setCursorPosition(player.getX(), player.getY());               //Printar även ut spelare när väggar flyttar
                    terminal.putCharacter(player.getPlayerChar());

                    //TODO inte printa om x > 79

                    for (Obstacle o : walls) {                                              //Printar väggar
                      //  printWall(terminal, o);
                        printComets(terminal, o);                                           //Printar kometer
                    }

                    printLives(terminal, lives);

                    for (Obstacle ob : walls) {                                             //Kollisionscheck med väggarna
                        for (Position p : ob.obstacleList) {
                            if (p.getX() == player.getX() && p.getY() == player.getY()) {
                                System.out.println("DEATH");    //TODO nåt bättre än detta
                                lives--;
                                terminal.setForegroundColor(TextColor.ANSI.WHITE);          //Skriver ut "OH NO!" där man krockar med vägg
                                terminal.setCursorPosition(player.getX(), player.getY());
                                String death = "OH NO!";
                                for (int i = 0; i < death.length(); i++) {
                                    terminal.putCharacter(death.charAt(i));
                                }
                            }
                        }
                    }
                    terminal.flush();
                }
                if (lives == 0){                    //Kollar ofta
                    break;
                }
            } while (keyStroke == null);

            if (lives == 0){                        //Bryter spelloopen
                terminal.clearScreen();
                printDeath(terminal);
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

    private static void printDeath(Terminal terminal) throws IOException {
        terminal.setCursorPosition(35, 12);
        terminal.setForegroundColor(TextColor.ANSI.RED);
        terminal.bell();
        String death = "YOU DIED!";
        for (int i = 0; i < death.length(); i++) {
            terminal.putCharacter(death.charAt(i));
        }
        terminal.flush();
    }
    private static void printLives(Terminal terminal, int lives) throws IOException {
        terminal.setCursorPosition(0, 0);
        terminal.setForegroundColor(TextColor.ANSI.GREEN);                  //Printar gröna liv - minskar per krock med objekt
        String livesLeft = "LIVES: ";
        for (int i = 0; i < livesLeft.length(); i++) {
            terminal.putCharacter(livesLeft.charAt(i));
        }
        for (int i = 0; i < lives; i++) {
            terminal.putCharacter('\u265e');
        }
        terminal.flush();
        terminal.setForegroundColor(TextColor.ANSI.MAGENTA);
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
            terminal.setForegroundColor(TextColor.ANSI.WHITE);
            if (p.getX() >= 0) {
                terminal.setCursorPosition(p.getX(), p.getY());
                terminal.putCharacter('X');
                p.setX(p.getX() - 1);
                terminal.flush();
            } else {
                o.obstacleList = buildWall(79 + r.nextInt(16));
                break;
            }
        }

    }
    private static List<Position> buildComet (int startX) {             //Initierar samt bygger nya kometer
        int startY = r.nextInt(20);
        int randY = r.nextInt(7);
        List<Integer> cometShape = new ArrayList<>();
        for (int i = 0; i < randY; i++) {
            cometShape.add(r.nextInt(10) +1);
        }
        List<Position> comets = new ArrayList<>();
        for (int i = 0; i < cometShape.size(); i++) {
            int x = startX-(cometShape.get(i)/2);
            for (int j = 0; j < cometShape.get(i); j++) {
               comets.add(new Position(x, startY));
                x++;
            }
            startY++;
        }

        return comets;
    }
    private static void printComets(Terminal terminal, Obstacle o) throws IOException {      //Printar kometer
        boolean everythingBelowZero=true;

        for (Position p : o.obstacleList) {
            terminal.setForegroundColor(TextColor.ANSI.YELLOW);
            if (p.getX() >= 0) {
                terminal.setCursorPosition(p.getX(), p.getY());
                terminal.putCharacter('O');
                p.setX(p.getX() - 1);
                terminal.flush();

                everythingBelowZero=false;
            }
        }
        if (everythingBelowZero) {                                                                    //Bygger om komet när den är utanför skärmen
            o.obstacleList = buildComet(79+r.nextInt(8));

        }
    }
}

