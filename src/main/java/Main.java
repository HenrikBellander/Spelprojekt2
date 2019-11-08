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
    static int starBlink = 0;
    static int points = 0;
    static int level = 1;
    static List<Obstacle> obstacles = createComets();                                  //Skapar kometer
    static List<Obstacle> obstacleslvl3 = createComets();                               //Dubbel lista på lvl 3
    static Obstacle boss = createBoss();
    static int bossHealth = 20;

    public static void main(String[] args) throws IOException, InterruptedException {
         String intro = "Bakgrund.wav";
         Sound soundPlayer = new Sound();
        soundPlayer.playSound(intro);

         String shoot = "Shoot.wav";





        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();      //Skapar terminalfönster
        Terminal terminal = terminalFactory.createTerminal();
        terminal.setCursorVisible(false);

        Player player = new Player();                                              //Skapar spelare och väggar
        int lives = 3;


        List<Shot> shotsFired = new ArrayList<>();                                  //För lagring av shots

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
                if (counter % 500 == 0 && speed > 5) {                               //För att få spelet att gå snabbare
                        speed--;
                }

                if (counter % speed == 0 || keyStroke != null) {
                    terminal.clearScreen();
                    printPlayer(terminal, player);

                    //TODO inte printa om x > 79

                    //TODO Beroende på level
                    for (Obstacle o : obstacles) {                                              //Printar väggar
                        if (level == 1) {
                            printComets(terminal, o);                                               //Printar kometer
                        } else if (level == 2) {
                            printWall(terminal, o);
                        } else if (level == 3) {
                            printWall(terminal, o);
                        }
                    }
                    if (level == 3){
                        for (Obstacle o : obstacleslvl3) {
                            printComets(terminal, o);
                        }
                    }
                    if (level == 4) {
                        if (counter%25==0) {
                            moveBoss(boss);
                        }
                        printBoss(terminal, boss);
                        printBossHealth(terminal, bossHealth);
                        bossCollisionCheck(boss, shotsFired);
                        if (bossHealth < 1){
                            endGame(terminal);
                        }
                    }


                    lives = collisionCheck(terminal, player, lives, obstacles);
                    if (level == 3) {
                        lives = collisionCheck(terminal, player, lives, obstacleslvl3);
                    }

                    points = eatStars(terminal, player, points, stars);

                    printShots(terminal, shotsFired);
                    printLives(terminal, lives);
                    printPoints(terminal, points);
                    printStars(terminal, stars);
                    shotCollisionCheck(obstacles, shotsFired);
                    if (level == 3) {
                        shotCollisionCheck(obstacleslvl3, shotsFired);
                    }


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
            playerMovement(player, type, shotsFired);
            terminal.flush();
        } while (true);                                                 //Gameloop slutar här
    }

    private static void endGame(Terminal terminal) throws IOException {
        terminal.clearScreen();
        terminal.setCursorPosition(35, 12);
        terminal.setForegroundColor(TextColor.ANSI.GREEN);
        String death = "YOU WIN!";
        for (int i = 0; i < death.length(); i++) {
            terminal.putCharacter(death.charAt(i));
        }
        terminal.flush();
    }

    private static void moveBoss(Obstacle boss) {
        boolean tooHigh = false;
        boolean tooLow = false;
        for (Position p : boss.obstacleList){
            if (p.getY() == 0){
                tooHigh = true;
            }
            if (p.getY() == 23){
                tooLow = true;
            }
        }
        int rand = r.nextInt(2);
            if (rand == 1 && tooLow == false) {
                for (Position p : boss.obstacleList) {
                    p.setY(p.getY() + 1);
                }
            } else if (rand == 0 && tooHigh == false){
                for (Position p : boss.obstacleList) {
                    p.setY(p.getY() - 1);
                }
            }
        }


    private static void printBoss(Terminal terminal, Obstacle boss) throws IOException {
        for (Position p : boss.obstacleList){
            terminal.setCursorPosition(p.getX(), p.getY());
            terminal.putCharacter('X');
        }
    }

    private static void levelTwo() {
        obstacles.clear();
        obstacles = createWalls();
        speed = 20;
        //TODO Level 2 Message
    }
    //TODO
    private static void levelBoss() {
        speed = 20;
        obstacleslvl3.clear();
        obstacles.clear();
    }

    private static Obstacle createBoss() {
        List<Position> bossParts = new ArrayList<>();
        int x = 72;
        int y = 8;
        for (int i = 0; i < 7; i++){
            bossParts.add(new Position(x, y));
            x++;
        }
        bossParts.add(new Position(75, 9));
        bossParts.add(new Position(75, 10));
        x = 74;
        y = 11;
        for (int i = 0; i < 3; i++){
            bossParts.add(new Position(x, y));
            x++;
        }
        x=73;
        y=12;
        for (int i = 0; i < 5; i++){
            bossParts.add(new Position(x, y));
            x++;
        }
        x = 74;
        y = 13;
        for (int i = 0; i < 3; i++){
            bossParts.add(new Position(x, y));
            x++;
        }
        bossParts.add(new Position(75, 14));
        bossParts.add(new Position(75, 15));
        x = 72;
        y = 16;
        for (int i = 0; i < 7; i++){
            bossParts.add(new Position(x, y));
            x++;
        }
        return new Obstacle(bossParts);
    }

    private static void levelThree() {
        speed = 20;
    }

    private static void shotCollisionCheck(List<Obstacle> obstacles, List<Shot> shots) {
        for (Obstacle ob : obstacles) {                                                                     //Kollisionscheck mellan shots och obstacles
            for (int i = 0; i < shots.size(); i++) {
                for (int p = 0; p < ob.obstacleList.size(); p++) {
                    try {
                        if (ob.obstacleList.get(p).getX() <= shots.get(i).getX() && ob.obstacleList.get(p).getY() == shots.get(i).getY()) {
                            shots.remove(i);
                            ob.obstacleList.remove(p);
                        }
                    }catch (IndexOutOfBoundsException e) {
                    }
                }
            }
        }
    }
    private static void bossCollisionCheck(Obstacle boss, List<Shot> shots) {
                                                                             //Kollisionscheck mellan shots och obstacles
            for (int i = 0; i < shots.size(); i++) {
                for (int p = 0; p < boss.obstacleList.size(); p++) {
                    try {
                        if (boss.obstacleList.get(p).getX() <= shots.get(i).getX() && boss.obstacleList.get(p).getY() == shots.get(i).getY()) {
                            shots.remove(i);
                            bossHealth--;
                        }
                    }catch (IndexOutOfBoundsException e) {
                    }
                }
            }
        }


    private static void printShots(Terminal terminal, List<Shot> shotsFired) throws IOException {           //Printar shots, åker i motsatt riktning som obstacles med samma hastighet

        terminal.setForegroundColor(TextColor.ANSI.CYAN);
        for (int i = 0; i < shotsFired.size(); i++) {
            if (shotsFired.get(i).getX() < 80) {
                                                                           //Printar bara shots på spelplanen, annars tas de bort ur spelet
                terminal.setCursorPosition(shotsFired.get(i).getX(), shotsFired.get(i).getY());
                terminal.putCharacter('\u2b50');
                shotsFired.get(i).setX(shotsFired.get(i).getX()+1);
            } else {
                shotsFired.remove(shotsFired.get(i));
            }
        }
        terminal.setForegroundColor(TextColor.ANSI.WHITE);
    }

    private static void playerMovement(Player player, KeyType type, List<Shot> shots) {
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
            case Tab:
                if (points > 0){
                    shots.add(new Shot(11, player.getY()));
                    points--;

                    String shoot = "Shoot.wav";
                    Sound soundPlayer2 = new Sound();
                    soundPlayer2.playSound(shoot);
                }
        }
    }

    private static int eatStars(Terminal terminal, Player player, int points, List<Position> stars) throws IOException {
        for (Position o : stars) {                                                                          //Lägger till ny stjärna när spelare har tagit en samt ökar poäng
            if (o.getX() == player.getX() && o.getY() == player.getY()) {                                   //Bestämmer också level
                points++;
                if (points >= 1){
                    if (level == 1){
                        levelTwo();
                        level = 2;
                    }
                }
                if (points >= 2){
                    if (level == 2){
                        levelThree();
                        level = 3;
                    }
                }
                if (points >= 4){
                    if (level == 3){
                        levelBoss();
                        level = 4;
                    }
                }
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
        for (Obstacle ob : obstacles) {                                             //Kollisionscheck med obstacles
            for (Position p : ob.obstacleList) {
                if (p.getX() == player.getX() && p.getY() == player.getY()) {
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
        terminal.setForegroundColor(TextColor.ANSI.MAGENTA);
        terminal.setCursorPosition(player.getX(), player.getY());               //Flytta spelare
        terminal.putCharacter(player.getPlayerChar());
        terminal.setForegroundColor(TextColor.ANSI.WHITE);
        terminal.flush();
    }

    private static List<Obstacle> createComets() {
        List<Obstacle> comets = new ArrayList<>();

        comets.add(new Obstacle(buildComet(79)));
        comets.add(new Obstacle(buildComet(95)));
        comets.add(new Obstacle(buildComet(111)));
        comets.add(new Obstacle(buildComet(127)));
        comets.add(new Obstacle(buildComet(143)));
        return comets;
    }
    private static List<Obstacle> createWalls() {
        List<Obstacle> walls = new ArrayList<>();

        walls.add(new Obstacle(buildWall(79)));
        walls.add(new Obstacle(buildWall(95)));
        walls.add(new Obstacle(buildWall(111)));
        walls.add(new Obstacle(buildWall(127)));
        walls.add(new Obstacle(buildWall(143)));
        return walls;
    }

    private static void printStars(Terminal terminal, List<Position> stars) throws IOException {
        for (Position o : stars) {
            starBlink++;
            if (o.getX() >= 0) {
                o.setX(o.getX() - 1);
                terminal.setCursorPosition(o.getX(), o.getY());
                if (starBlink % 2 == 0) {
                    terminal.setForegroundColor(TextColor.ANSI.WHITE);
                } else {
                    terminal.setForegroundColor(TextColor.ANSI.CYAN);
                }
                terminal.putCharacter('\u2b50');            //25CF
            } else {
                o.setX(r.nextInt(20)+60);
                o.setY(r.nextInt(24));
            }
        }
        terminal.flush();
        terminal.setForegroundColor(TextColor.ANSI.WHITE);
    }

    private static void printPoints(Terminal terminal, int points) throws IOException {
        terminal.setForegroundColor(TextColor.ANSI.CYAN);
        String pts = "STARS: " + points;
        terminal.setCursorPosition(0, 1);
        for (int i = 0; i < pts.length(); i++) {
            terminal.putCharacter(pts.charAt(i));
        }
        terminal.setForegroundColor(TextColor.ANSI.WHITE);

    }
    private static void printBossHealth(Terminal terminal, int bossHealth) throws IOException {
        terminal.setForegroundColor(TextColor.ANSI.RED);
        String health = "BOSS HEALTH: ";
        terminal.setCursorPosition(56, 0);
        for (int i = 0; i < health.length(); i++) {
            terminal.putCharacter(health.charAt(i));
        }
        terminal.setCursorPosition(56, 1);
        for (int i = 0; i < bossHealth; i++) {
            terminal.putCharacter('\u29f3');
        }
        terminal.setForegroundColor(TextColor.ANSI.WHITE);

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

