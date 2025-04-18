import javax.swing.JFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class App {
    public static void main(String[] args) {
    
        final int tileSize = 32; // pixel size of each tile 
        final int columnCount = 19; 
        final int rowCount = 21;
        
        // Create game instance
        PACMAN pacmanGame = new PACMAN();
        
        // Configure main window
        JFrame frame = new JFrame("Pac Man");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(columnCount * tileSize, rowCount * tileSize);
        frame.setLocationRelativeTo(null); 

        // game panel and setup
        frame.add(pacmanGame);
        frame.pack();
        pacmanGame.requestFocus(); // for keyboard input
        frame.setVisible(true);

        // saving the gamestate when we close the window
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                pacmanGame.saveGame();

            }
        });
    }
}
