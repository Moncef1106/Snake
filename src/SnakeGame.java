import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener {

   // Constants for game dimensions
   private static final int WIDTH = 300;
   private static final int HEIGHT = 300;
   private static final int UNIT_SIZE = 10;
   private static final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
   private static final int DELAY = 75;

   // Arrays to store snake coordinates
   private final int[] x = new int[GAME_UNITS];
   private final int[] y = new int[GAME_UNITS];

   // Initial snake body parts, apples eaten, and apple coordinates
   private int bodyParts = 6;
   private int applesEaten;
   private int appleX;
   private int appleY;

   // Initial snake direction and game state
   private char direction = 'R';  // 'U' for up, 'D' for down, 'L' for left, 'R' for right
   private boolean running = false;

   // Timer for game updates
   private Timer timer;


   
   // Constructor for SnakeGame class
   public SnakeGame() {
       setPreferredSize(new Dimension(WIDTH, HEIGHT));
       setBackground(Color.GREEN);
       setFocusable(true);
       addKeyListener(new MyKeyAdapter());
       startGame();
   }

   private void startGame() {
    newApple();
    running = true;
    timer = new Timer(DELAY, this);
    timer.start();
   }

   // Override the paintComponent method to draw the game elements
   @Override
   public void paintComponent(Graphics g) {
       super.paintComponent(g);
       draw(g);
   }

   // Method to draw the game elements
   private void draw(Graphics g) {
       if (running) {
           // Draw the apple
           g.setColor(Color.RED);
           g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

           // Draw the snake
           for (int i = 0; i < bodyParts; i++) {
               if (i == 0) {
                g.setColor(new Color(63, 117, 252));  
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
               } else {
                   g.setColor(new Color(63, 117, 252));  
                   g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
               }
           }

           // Display the score
           g.setColor(Color.WHITE);
           g.setFont(new Font("Arial", Font.BOLD, 14));
           FontMetrics metrics = getFontMetrics(g.getFont());
           g.drawString("Score: " + applesEaten, (WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
       } else {
           // If the game is over, display game over message and score
           gameOver(g);
       }
   }

   // Method to generate a new apple at a random location
   private void newApple() {
       appleX = (int) (Math.random() * (WIDTH / UNIT_SIZE)) * UNIT_SIZE;
       appleY = (int) (Math.random() * (HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
   }

   // Method to update the snake's position based on the current direction
   private void move() {
       for (int i = bodyParts; i > 0; i--) {
           // Move each body part to the position of the one in front of it
           x[i] = x[i - 1];
           y[i] = y[i - 1];
       }

       // Move the head of the snake based on the current direction
       switch (direction) {
           case 'U':
               y[0] = y[0] - UNIT_SIZE;
               break;
           case 'D':
               y[0] = y[0] + UNIT_SIZE;
               break;
           case 'L':
               x[0] = x[0] - UNIT_SIZE;
               break;
           case 'R':
               x[0] = x[0] + UNIT_SIZE;
               break;
       }
   }

   // Method to check if the snake has eaten the apple
   private void checkApple() {
       if ((x[0] == appleX) && (y[0] == appleY)) {
           bodyParts++;  // Increase the length of the snake
           applesEaten++;  // Increment the score
           newApple();  // Place a new apple on the game board
       }
   }

   // Method to check for collisions with walls or itself
   private void checkCollisions() {
    for (int i = bodyParts; i > 0; i--) {
        if ((x[0] == x[i]) && (y[0] == y[i])) {
            running = false;
        }
    }

    if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
        running = false;
    }

    if (!running) {
        timer.stop();
        gameOver(); // Call the gameOver method to handle game over actions
    }
}

private void gameOver() {
    // Display game over message and score
    repaint();
    JOptionPane.showMessageDialog(this, "Game Over\nScore: " + applesEaten, "Game Over", JOptionPane.PLAIN_MESSAGE);

    // Ask user if they want to play again
    int option = JOptionPane.showConfirmDialog(this, "Wil je opnieuw beginnen?", "Herstart Game", JOptionPane.YES_NO_OPTION);
    if (option == JOptionPane.YES_OPTION) {
        // If user wants to play again, restart the game
        resetGame();
    } else {
        // If user chooses not to play again, exit the application
        System.exit(0);
    }
}

private void resetGame() {
    // Reset variables to initial values
    bodyParts = 6;
    applesEaten = 0;
    direction = 'R';
    running = false;
    for (int i = 0; i < bodyParts; i++) {
        x[i] = 0;
        y[i] = 0;
    }
    startGame(); // Start the game again
}

   // Method to display the game over message
   private void gameOver(Graphics g) {
       g.setColor(Color.WHITE);
       g.setFont(new Font("Arial", Font.BOLD, 20));
       FontMetrics metrics1 = getFontMetrics(g.getFont());
       g.drawString("Game Over", (WIDTH - metrics1.stringWidth("Game Over")) / 2, HEIGHT / 2);
       g.setColor(Color.WHITE);
       g.setFont(new Font("Arial", Font.BOLD, 14));
       FontMetrics metrics2 = getFontMetrics(g.getFont());
       g.drawString("Score: " + applesEaten, (WIDTH - metrics2.stringWidth("Je score is: " + applesEaten)) / 2, g.getFont().getSize());
   }

   // ActionListener interface method for timer updates
   @Override
   public void actionPerformed(ActionEvent e) {
       if (running) {
           move();  // Move the snake
           checkApple();  // Check for apple eating
           checkCollisions();  // Check for collisions
       }
       repaint();  // Redraw the game board
   }

   // Inner class to handle keyboard input
   private class MyKeyAdapter extends KeyAdapter {
       @Override
       public void keyPressed(KeyEvent e) {
           // Change the direction based on arrow key input
           switch (e.getKeyCode()) {
               case KeyEvent.VK_LEFT:
                   if (direction != 'R') {
                       direction = 'L';
                   }
                   break;
               case KeyEvent.VK_RIGHT:
                   if (direction != 'L') {
                       direction = 'R';
                   }
                   break;
               case KeyEvent.VK_UP:
                   if (direction != 'D') {
                       direction = 'U';
                   }
                   break;
               case KeyEvent.VK_DOWN:
                   if (direction != 'U') {
                       direction = 'D';
                   }
                   break;
           }
       }
   }

   // Main method to create and display the game frame
   public static void main(String[] args) {
       JFrame frame = new JFrame("Snake Game van Moncef");
       SnakeGame game = new SnakeGame();
       frame.add(game);
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setResizable(false);
       frame.pack();
       frame.setLocationRelativeTo(null);
       frame.setVisible(true);
   }
}
