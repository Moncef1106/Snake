import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;
    private static final int UNIT_SIZE = 10;
    private static final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 75;

    private Timer timer;
    private Snake snake;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private boolean running;

    class Snake {
        private int[] x;
        private int[] y;
        private int bodyParts;
        private char direction;

        public Snake(int initialLength, int startX, int startY) {
            x = new int[SnakeGame.GAME_UNITS];
            y = new int[SnakeGame.GAME_UNITS];
            bodyParts = initialLength;
            direction = 'R';

            for (int i = 0; i < initialLength; i++) {
                x[i] = startX - i * SnakeGame.UNIT_SIZE;
                y[i] = startY;
            }
        }

        public int getBodyParts() {
            return bodyParts;
        }

        public int[] getX() {
            return x;
        }

        public int[] getY() {
            return y;
        }

        public void move() {
            for (int i = bodyParts; i > 0; i--) {
                x[i] = x[i - 1];
                y[i] = y[i - 1];
            }

            switch (direction) {
                case 'U':
                    y[0] -= SnakeGame.UNIT_SIZE;
                    break;
                case 'D':
                    y[0] += SnakeGame.UNIT_SIZE;
                    break;
                case 'L':
                    x[0] -= SnakeGame.UNIT_SIZE;
                    break;
                case 'R':
                    x[0] += SnakeGame.UNIT_SIZE;
                    break;
            }
        }

        public boolean checkSelfCollision() {
            for (int i = bodyParts; i > 0; i--) {
                if (x[0] == x[i] && y[0] == y[i]) {
                    return true;
                }
            }
            return false;
        }

        public boolean checkWallCollision(int width, int height) {
            return x[0] < 0 || x[0] >= width || y[0] < 0 || y[0] >= height;
        }

        public char getDirection() {
            return direction;
        }

        public void setBodyParts(int newBodyParts) {
            bodyParts = newBodyParts;
        }
        

        public void setDirection(char newDirection) {
            direction = newDirection;
        }
    }
    

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.GREEN);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        startGame();
    }

    private void startGame() {
        snake = new Snake(3, WIDTH / 2, HEIGHT / 2);
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (running) {
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < snake.getBodyParts(); i++) {
                if (i == 0) {
                    g.setColor(new Color(16, 77, 230));
                    g.fillRect(snake.getX()[i], snake.getY()[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(63, 117, 252));
                    g.fillRect(snake.getX()[i], snake.getY()[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    private void newApple() {
        appleX = (int) (Math.random() * (WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = (int) (Math.random() * (HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    private void move() {
        snake.move();
    }

    private void checkApple() {
        if ((snake.getX()[0] == appleX) && (snake.getY()[0] == appleY)) {
            snake.setBodyParts(snake.getBodyParts() + 1);
            applesEaten++;
            newApple();
        }
    }

    private void checkCollisions() {
        if (snake.checkSelfCollision() || snake.checkWallCollision(WIDTH, HEIGHT)) {
            running = false;
        }

        if (!running) {
            timer.stop();
            gameOver();
        }
    }

    private void gameOver() {
        repaint();
        JOptionPane.showMessageDialog(this, "Game Over\nScore: " + applesEaten, "Game Over", JOptionPane.PLAIN_MESSAGE);
        int option = JOptionPane.showConfirmDialog(this, "Wil je opnieuw beginnen?", "Herstart Game", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            System.exit(0);
        }
    }

    private void resetGame() {
        applesEaten = 0;
        running = false;
        snake = new Snake(3, WIDTH / 2, HEIGHT / 2);
        startGame();
    }

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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (snake.getDirection() != 'R') {
                        snake.setDirection('L');
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (snake.getDirection() != 'L') {
                        snake.setDirection('R');
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (snake.getDirection() != 'D') {
                        snake.setDirection('U');
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (snake.getDirection() != 'U') {
                        snake.setDirection('D');
                    }
                    break;
            }
        }
    }

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
