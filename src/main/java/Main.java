import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import java.io.IOException;
import java.util.Random;

public class Main {

    Random r = new Random();

    public static void main(String[] args) throws IOException, InterruptedException {

        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();      //Skapar terminalfönster
        Terminal terminal = terminalFactory.createTerminal();

        Player player = new Player ();                                              //Skapar spelare
        terminal.setCursorVisible(false);
        terminal.setForegroundColor(TextColor.ANSI.WHITE);


        do {
            terminal.clearScreen();
            terminal.setCursorPosition(player.getX(), player.getY());
            terminal.putCharacter(player.getPlayerChar());
            terminal.flush();

            KeyStroke keyStroke = null;
            do {
                Thread.sleep(5); // might throw InterruptedException
                keyStroke = terminal.pollInput();
            } while (keyStroke == null);

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
}

