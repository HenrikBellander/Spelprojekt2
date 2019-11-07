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

        Player player = new Player();                                              //Skapar spelare och väggar
        int lives = 3;
        int points = 0;

        List<Obstacle> obstacles = createComets();                                  //Skapar kometer

        List<Position> stars = new ArrayList<>();                                   //Skapar och placerar de första stjärnorna
        for (int i = 0; i < 5; i++) {
            stars.add(new Position(r.nextInt(20)+60, r.nextInt(24)));
        }

        int counter = 0;                                                            //Counter bestämmer hur ofta väggar flyttar sig, i princip dess hastighet. Ökar varje 5ms loop.
        do {                                                                        //Gameloopen

            printPlayer(terminal, player);

            KeyStroke keyStroke = null;
            do {
                Thread.sleep(5);
                keyStroke = terminal.pollInput();
                counter++;
                if (counter % 300 == 0 && speed > 3) {                               //För att få spelet att gå snabbare
                        speed--;
                }

                if (counter % speed == 0 || keyStroke != null) {
                    terminal.clearScreen();
                    printPlayer(terminal, player);

                    //TODO inte printa om x > 79

                    //TODO Beroende på level
                    for (Obstacle o : obstacles) {                                              //Printar väggar
                        //  printWall(terminal, o);
                        printComets(terminal, o);                                               //Printar kometer
                    }



                    lives = collisionCheck(terminal, player, lives, obstacles);
                    points = eatStars(terminal, player, points, stars);

                    printLives(terminal, lives);
                    printPoints(terminal, points);
                    printStars(terminal, stars);

                    terminal.flush();
                }
                if (lives == 0) {                    //Kollar ofta
                    break;
                }
            } while (keyStroke == null);             //Avslutar 5ms loop

            if (lives == 0) {                        //Bryter spelloopen
                //terminal.clearScreen();
                printDeath(terminal);
                break;
            }
            terminal.setCursorPosition(player.getX(), player.getY());                           //Suddar spelare efter knapptryck
            terminal.putCharacter(' ');

            KeyType type = keyStroke.getKeyType();
            playerMovement(player, type);
            terminal.flush();
        } while (true);                                                 //Gameloop slutar här
    }

    private static void playerMovement(Player player, KeyType type) {
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
                    player.setY(player.getY() - 1);
                }
                break;
        }
    }

    private static int eatStars(Terminal terminal, Player player, int points, List<Position> stars) throws IOException {
        for (Position o : stars) {                                                                        //Lägger till ny stjärna när spelare har tagit en
            if (o.getX() == player.getX() && o.getY() == player.getY()) {
                points++;
                o.setX(r.nextInt(20)+60);
                o.setY(r.nextInt(24));
                terminal.setCursorPosition(stars.get(stars.size() - 1).getX(), stars.get(stars.size() - 1).getY());
                terminal.setForegroundColor(TextColor.ANSI.GREEN);
                terminal.putCharacter('\u2b50');
                terminal.setForegroundColor(TextColor.ANSI.BLACK);
                break;
            }
        }
        return points;
    }

    private static int collisionCheck(Terminal terminal, Player player, int lives, List<Obstacle> obstacles) throws IOException {
        for (Obstacle ob : obstacles) {                                             //Kollisionscheck med väggarna
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
        return lives;
    }

    private static void printPlayer(Terminal terminal, Player player) throws IOException {
        terminal.setForegroundColor(TextColor.ANSI.WHITE);
        terminal.setCursorPosition(player.getX(), player.getY());               //Flytta spelare
        terminal.putCharacter(player.getPlayerChar());
        terminal.flush();
    }

    private static List<Obstacle> createComets() {
        List<Obstacle> obstacles = new ArrayList<>();

        obstacles.add(new Obstacle(buildComet(79)));
        obstacles.add(new Obstacle(buildComet(95)));
        obstacles.add(new Obstacle(buildComet(111)));
        obstacles.add(new Obstacle(buildComet(127)));
        obstacles.add(new Obstacle(buildComet(143)));
        return obstacles;
    }

    private static void printStars(Terminal terminal, List<Position> stars) throws IOException {
        for (Position o : stars) {
            if (o.getX() >= 0) {
                o.setX(o.getX() - 1);
                terminal.setCursorPosition(o.getX(), o.getY());
                terminal.setForegroundColor(TextColor.ANSI.WHITE);
                terminal.putCharacter('\u2b50');            //25CF
                /*terminal.setForegroundColor(TextColor.ANSI.WHITE);        //Ev TODO: Blinkande stjärnor
                terminal.putCharacter('\u2b50');
                terminal.flush();*/
            } else {
                o.setX(r.nextInt(20)+60);
                o.setY(r.nextInt(24));
            }
        }
        terminal.flush();
    }

    private static void printPoints(Terminal terminal, int points) throws IOException {
        String pts = "Points: " + points;
        terminal.setCursorPosition(0, 1);
        for (int i = 0; i < pts.length(); i++) {
            terminal.putCharacter(pts.charAt(i));
        }
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
        terminal.setForegroundColor(TextColor.ANSI.WHITE);
    }

    private static List<Position> buildWall(int startX) {
        int y = 0;
        int rand = r.nextInt(14);
        List<Position> walls = new ArrayList<>();                   //Initierar samt bygger nya walls efter att de nått slutet av sin resa
        for (int i = 0; i < 24; i++) {
            if (i < rand || i >= rand + 10) {
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
            } else {
                o.obstacleList = buildWall(79 + r.nextInt(16));
                break;
            }
        }
        terminal.flush();
    }

    private static List<Position> buildComet(int startX) {             //Initierar samt bygger nya kometer
        int startY = r.nextInt(20);
        int randY = r.nextInt(7);
        List<Integer> cometShape = new ArrayList<>();
        for (int i = 0; i < randY; i++) {
            cometShape.add(r.nextInt(10) + 1);
        }
        List<Position> comets = new ArrayList<>();
        for (int i = 0; i < cometShape.size(); i++) {
            int x = startX - (cometShape.get(i) / 2);
            for (int j = 0; j < cometShape.get(i); j++) {
                comets.add(new Position(x, startY));
                x++;
            }
            startY++;
        }
        return comets;
    }

    private static void printComets(Terminal terminal, Obstacle o) throws IOException {      //Printar kometer
        boolean everythingBelowZero = true;
        for (Position p : o.obstacleList) {
            terminal.setForegroundColor(TextColor.ANSI.YELLOW);
            if (p.getX() >= 0) {
                terminal.setCursorPosition(p.getX(), p.getY());
                terminal.putCharacter('O');
                p.setX(p.getX() - 1);
                everythingBelowZero = false;
            }
        }
        if (everythingBelowZero) {                                                                    //Bygger om komet när den är utanför skärmen
            o.obstacleList = buildComet(79 + r.nextInt(8));
        }
        terminal.setForegroundColor(TextColor.ANSI.WHITE);
        terminal.flush();
    }
}

