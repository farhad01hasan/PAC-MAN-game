import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;
import java.util.prefs.Preferences;


public class PACMAN extends JPanel implements ActionListener, KeyListener {


    //  Game state management
    public enum GameState { MENU, PLAYING, GAME_OVER }
    public GameState gameState = GameState.MENU;
   
    //  Persistent data storage
    private final Preferences prefs = Preferences.userNodeForPackage(PACMAN.class);


    class Block {
      private   int x, y, width, height;
      private   Image image;
      private   int startx, starty;
      private   char direction = 'U'; // U D L R
      private   int velocityX = 0, velocityY = 0;


        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startx = x;
            this.starty = y;
        }


        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
           
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }


        void updateVelocity() {
            switch (this.direction) {
                case 'U' -> { velocityX = 0; velocityY = -tilesize/4; }
                case 'D' -> { velocityX = 0; velocityY = tilesize/4; }
                case 'L' -> { velocityX = -tilesize/4; velocityY = 0; }
                case 'R' -> { velocityX = tilesize/4; velocityY = 0; }
            }
        }
       
        void reset() {
            this.x = this.startx;
            this.y = this.starty;
        }
    }


    // Game constants
    private  int rowcount = 21;
    private  int columncount = 19;
    private  int tilesize = 32;
    private  int boardwidth = columncount * tilesize;
    private  int boardheight = rowcount * tilesize;


    // Images
    private Image wallImage;
    private Image blueGhostImage, orangeGhostImage, pinkGhostImage, redGhostImage;
    private Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;


    // Game objects
    private HashSet<Block> walls = new HashSet<>();
    private HashSet<Block> foods = new HashSet<>();
    private HashSet<Block> ghosts = new HashSet<>();
    private Block pacman;


    // Game logic


     Timer gameloop;


     char[] directions = {'U','D','L','R'};
     Random random = new Random();


    int score = 0;
    int lives = 3;
    boolean gameover = false;


    //  map for the game
    
    private  String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX"
    };


    public PACMAN() {


        setPreferredSize(new Dimension(boardwidth, boardheight));
        setBackground(Color.BLACK);


        addKeyListener(this);
        setFocusable(true);


        // Load images


        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();


        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();


        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();


        loadmap();
        gameloop = new Timer(50, this);
    }


    //  interface design for the menu
private void drawMenu(Graphics g) {

      // Background
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, boardwidth, boardheight);
 
      // fonts
      Font titleFont = new Font("Arial", Font.BOLD, 36);
      Font menuFont = new Font("Arial", Font.BOLD, 24);


      g.setFont(menuFont); 
 
      FontMetrics titleMetrics = g.getFontMetrics(titleFont);
      FontMetrics menuMetrics = g.getFontMetrics(menuFont);
 
      // Strings
      String title = "PAC-MAN";
      String option1 = "1. NEW GAME";
      String option2 = "2. CONTINUE";
      String highScore = "HIGH SCORE: " + prefs.getInt("highScore", 0);
 
     
      int spacing = 20;  // space between lines
 

      int totalTextHeight = titleMetrics.getHeight() + spacing +
                            menuMetrics.getHeight() + spacing +
                            menuMetrics.getHeight() + spacing +
                            menuMetrics.getHeight();
 

      int startY = (boardheight - totalTextHeight) / 2;

 
      // Draw title
      g.setFont(titleFont);
      g.setColor(Color.YELLOW);
      g.drawString(title,
          (boardwidth - titleMetrics.stringWidth(title)) / 2,
          startY + titleMetrics.getAscent());
 
      // Draw menu options
      g.setFont(menuFont);
      g.setColor(Color.WHITE);
      int y = startY + titleMetrics.getHeight() + spacing;
 
    g.drawString(option1,
          (boardwidth - menuMetrics.stringWidth(option1)) / 2,
          y + menuMetrics.getAscent());
          y += menuMetrics.getHeight() + spacing;
          
    g.drawString(option2,
          (boardwidth - menuMetrics.stringWidth(option2)) / 2,
          y + menuMetrics.getAscent());
 

      // Draw high score
      y += menuMetrics.getHeight() + spacing;
      g.setColor(Color.CYAN);
      g.drawString(highScore,
          (boardwidth - menuMetrics.stringWidth(highScore)) / 2,
          y + menuMetrics.getAscent());
  }
 


    //  Save/load system
    public void saveGame() {
        try {
            // Save Pac-Man state
            prefs.putInt("pacmanX", pacman.x);
            prefs.putInt("pacmanY", pacman.y);
            prefs.putInt("pacmanDir", pacman.direction);
           
            // Save game status
            prefs.putInt("lives", lives);
            prefs.putInt("score", score);
           
            // Save food pellet positions
            StringBuilder foodData = new StringBuilder(rowcount * columncount);
            for (int r = 0; r < rowcount; r++) {
                for (int c = 0; c < columncount; c++) {
                    final int x = c * tilesize + 14;
                    final int y = r * tilesize + 14;
                    boolean exists = foods.stream().anyMatch(f ->
                        f.x == x && f.y == y && f.width == 4 && f.height == 4);
                    foodData.append(exists ? '1' : '0');
                }
            }
            prefs.put("foodData", foodData.toString());
           
            // Update high score
            prefs.putInt("highScore", Math.max(score, prefs.getInt("highScore", 0)));
        } catch (Exception e) {
            System.err.println("Save failed: " + e.getMessage());
        }
    }


    private boolean loadGame() {
        try {
            // Load Pac-Man
            pacman.x = prefs.getInt("pacmanX", pacman.startx);
            pacman.y = prefs.getInt("pacmanY", pacman.starty);
            pacman.direction = (char)prefs.getInt("pacmanDir", 'R');
            pacman.updateVelocity();
           
            // validation for loading game 
            lives = prefs.getInt("lives", 3);
            if (lives <= 0) {
                return false;
            }
            score = prefs.getInt("score", 0);
           
            // Reload food
            foods.clear();
            String foodData = prefs.get("foodData", "");
            for (int r = 0; r < rowcount; r++) {
                for (int c = 0; c < columncount; c++) {
                    if (foodData.charAt(r*columncount + c) == '1') {
                        foods.add(new Block(null, c*tilesize+14, r*tilesize+14, 4, 4));
                    }
                }
            }


            for (Block ghost : ghosts) {
               ghost.updateDirection(directions[random.nextInt(4)]);
               ghost.updateVelocity();
           }
         
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    //  Enhanced drawing

    public void paintComponent(Graphics g) {

        super.paintComponent(g);
       
    switch (gameState) {
case MENU -> drawMenu(g);
   
case PLAYING -> {
                
                
    for (Block wall : walls){
         g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height,null);
                }
 
    for (Block food : foods) {
        g.setColor(Color.WHITE);
        g.fillRect(food.x, food.y, food.width, food.height);
                }

        g.drawImage(pacman.image, pacman.x, pacman.y,pacman.width,pacman.height, null);


    for (Block ghost : ghosts) {
        g.drawImage (ghost.image, ghost.x, ghost.y,ghost.width,ghost.height, null);
               }    
        // displaying score and lives on heads-up-display
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 18));
                g.drawString("Lives: " + lives + "  Score: " + score, 10, 20);
            }
           
case GAME_OVER -> {
   g.setColor(Color.BLACK);
   g.fillRect(0, 0, boardwidth, boardheight);
   
   int centerX = boardwidth / 2;
   int centerY = boardheight / 2;
   
g.setColor(Color.RED);
   Font gameOverFont = new Font("Arial", Font.BOLD, 36);
   g.setFont(gameOverFont);
   FontMetrics fm = g.getFontMetrics();
   g.drawString("GAME OVER",
       centerX - fm.stringWidth("GAME OVER") / 2,
       centerY - 40);
   
g.setColor(Color.WHITE);
   Font scoreFont = new Font("Arial", Font.BOLD, 24);
   g.setFont(scoreFont);
   fm = g.getFontMetrics();
   String scoreText = "Score: " + score;
   g.drawString(scoreText,
       centerX - fm.stringWidth(scoreText) / 2,
       centerY + 10);
   

   Font continueFont = new Font("Arial", Font.PLAIN, 16);
   g.setFont(continueFont);
   fm = g.getFontMetrics();
   g.drawString("Press SPACE to Play again",
       centerX - fm.stringWidth("Press SPACE to Play again") / 2,
       centerY + 60);
}
        }
    }

    // handling the inputs for the game

    @Override
    public void keyReleased(KeyEvent e) {
        switch (gameState) {
            case MENU -> handleMenuInput(e);
            case PLAYING -> handleGameInput(e);

            case GAME_OVER -> {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                   
                    loadmap();
                    resetPositions();
                    lives = 3;
                    score = 0;
                    gameover = false;
                    gameState = GameState.PLAYING;
                    gameloop.start();
                }
            }
        }
    }


    private void handleMenuInput(KeyEvent e) {
    switch (e.getKeyCode()) {
        case KeyEvent.VK_1 , KeyEvent.VK_NUMPAD1 -> startNewGame();
            
        case KeyEvent.VK_2 , KeyEvent.VK_NUMPAD2-> {
                if (!loadGame()) {
                    JOptionPane.showMessageDialog(this, "No saved game found!");
                } else {
                    gameState = GameState.PLAYING;
                    gameloop.start();
                }
            }
        case KeyEvent.VK_3 , KeyEvent.VK_NUMPAD3 ->
                JOptionPane.showMessageDialog(this, "High Score: " + prefs.getInt("highScore", 0));
        }
    }


    private void handleGameInput(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP , KeyEvent.VK_W-> pacman.updateDirection('U');
            case KeyEvent.VK_DOWN , KeyEvent.VK_S-> pacman.updateDirection('D');
            case KeyEvent.VK_LEFT , KeyEvent.VK_A-> pacman.updateDirection('L');
            case KeyEvent.VK_RIGHT , KeyEvent.VK_D-> pacman.updateDirection('R');
        }
       // updating pacman image as per its directions
    pacman.image = switch (pacman.direction) {
        case 'U' -> pacmanUpImage;
        case 'D' -> pacmanDownImage;
        case 'L' -> pacmanLeftImage;
        default -> pacmanRightImage;

        };
    }


    private void startNewGame() {
        loadmap();
        resetPositions();
        lives = 3;
        score = 0;
        gameover = false;
        gameState = GameState.PLAYING;
        gameloop.start();
    }



    public void loadmap() {

      walls.clear();
      foods.clear();
      ghosts.clear();
 
      for (int r = 0; r < rowcount; r++) {
          for (int c = 0; c < columncount; c++) {

              String row = tileMap[r];
              char tile = row.charAt(c);
              int x = c * tilesize;
              int y = r * tilesize;
 
        switch (tile) {
            case 'X' -> walls.add(new Block(wallImage, x, y, tilesize, tilesize));


            case 'b' -> ghosts.add(new Block(blueGhostImage, x, y, tilesize, tilesize));
            case 'o' -> ghosts.add(new Block(orangeGhostImage, x, y, tilesize, tilesize));
            case 'p' -> ghosts.add(new Block(pinkGhostImage, x, y, tilesize, tilesize));
            case 'r' -> ghosts.add(new Block(redGhostImage, x, y, tilesize, tilesize));
                 
            case 'P' -> pacman = new Block(pacmanRightImage, x, y, tilesize, tilesize);


            case ' ' -> foods.add(new Block(null, x + tilesize/2 - 2, y + tilesize/2 - 2, 4, 4));


              }
          }
      }
  }    


  public void move() {

   // Pac-Man movement
   pacman.x += pacman.velocityX;
   pacman.y += pacman.velocityY;


   // Wall collision
   for (Block wall : walls) {
       if (collision(pacman, wall)) {
           pacman.x -= pacman.velocityX;
           pacman.y -= pacman.velocityY;
           break;
       }
   }


// teleporting 

    if (pacman.x < -tilesize) pacman.x = boardwidth;
    else if (pacman.x > boardwidth) pacman.x = -tilesize;


    if (pacman.y < -tilesize) pacman.y = boardheight;
    else if (pacman.y > boardheight) pacman.y = -tilesize;



   // Ghost movement
   for (Block ghost : ghosts) {


       ghost.x += ghost.velocityX;
       ghost.y += ghost.velocityY;


       // Ghost-wall collision
    for (Block wall : walls) {
        if (collision(ghost, wall)) {

               ghost.x -= ghost.velocityX;
               ghost.y -= ghost.velocityY;
               
               ghost.updateDirection(directions[random.nextInt(4)]);
               break;
           }
       }


       // Ghost-Pac-Man collision
       if (collision(ghost, pacman)) {

           lives--;
           if (lives <= 0)  
           gameover = true;
           resetPositions();

       }

// to prvent ghosts from  getting  stuck on the 10th row teleporting from one end to another 

    if(ghost.y == tilesize*9 && ghost.direction != 'U' && ghost.direction != 'D')
      {
        ghost.updateDirection(random.nextBoolean() ? 'U' : 'D');
      }

       // Ghost teleport

       if (ghost.x < -tilesize) ghost.x = boardwidth;
       else if (ghost.x > boardwidth) ghost.x = -tilesize;
       
       if (ghost.y < -tilesize) ghost.y = boardheight;
       else if (ghost.y > boardheight) ghost.y = -tilesize;


   }


   // Food collection
   foods.removeIf(food -> {
       if (collision(pacman, food)) {
           score += 10;
           return true;
       }
       return false;
   });


   if (foods.isEmpty()) {
       loadmap(); // Level complete
       resetPositions();
   }
}




public boolean collision(Block a, Block b) {
   if (a == null || b == null) return false;
   return a.x < b.x + b.width &&
          a.x + a.width > b.x &&
          a.y < b.y + b.height &&
          a.y + a.height > b.y;
}    


public boolean isWallAt(int y, int x) {
   int tileX = x / tilesize;
   int tileY = y / tilesize;
   
   if (tileX < 0 || tileX >= columncount || tileY < 0 || tileY >= rowcount)
       return true;

   return tileMap[tileY].charAt(tileX) == 'X';
}


public void resetPositions() {
  
   pacman.reset();

   pacman.velocityX = 0;
   pacman.velocityY = 0;


   for (Block ghost : ghosts) {
       ghost.reset();
       ghost.updateDirection(directions[random.nextInt(4)]);
   }

}



    @Override
    public void actionPerformed(ActionEvent e) {

        if (gameState == GameState.PLAYING) {
            move();
            if (gameover) {
                gameState = GameState.GAME_OVER;
                gameloop.stop();
            }
        }
        repaint();
    }


    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {}
}





