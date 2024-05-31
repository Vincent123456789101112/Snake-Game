import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SnakeGame extends JFrame {

    private final int WIDTH = 300, HEIGHT = 300;
    private final int DOT_SIZE = 10;
    private int[] x = new int[900];
    private int[] y = new int[900];
    private int bodyParts = 3;
    private int foodX;
    private int foodY;
    private int blueFoodX;
    private int blueFoodY;
    private int fruitsEaten = 0;
    private int highScore = 0;
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private JPanel gamePanel;
    private int speedUpTimer = 0;

    public SnakeGame() {
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                doDrawing(g);
                showScore(g);
            }
        };
        gamePanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        add(gamePanel);
        pack();
        setLocationRelativeTo(null);

        initGame();
        initKeyBindings();
    }

    private void initGame() {
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 50 - i * 10;
            y[i] = 50;
        }
        placeFood();
        placeBlueFoodRandomly();
        timer = new Timer(60, e -> gameUpdate());
        timer.start();
        running = true;
        fruitsEaten = 0;
    }

    private void initKeyBindings() {
        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "moveLeft");
        gamePanel.getActionMap().put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (direction != 'R') direction = 'L';
            }
        });

        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "moveRight");
        gamePanel.getActionMap().put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (direction != 'L') direction = 'R';
            }
        });

        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "moveUp");
        gamePanel.getActionMap().put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (direction != 'D') direction = 'U';
            }
        });

        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "moveDown");
        gamePanel.getActionMap().put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (direction != 'U') direction = 'D';
            }
        });

        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "restartGame");
        gamePanel.getActionMap().put("restartGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!running) restartGame();
            }
        });
    }

    private void placeFood() {
        int r = (int) (Math.random() * ((WIDTH - DOT_SIZE) / DOT_SIZE));
        foodX = r * DOT_SIZE;
        int y = (int) (Math.random() * ((HEIGHT - DOT_SIZE) / DOT_SIZE));
        foodY = y * DOT_SIZE;
    }

    private void placeBlueFoodRandomly() {
        int r = (int) (Math.random() * ((WIDTH - DOT_SIZE) / DOT_SIZE));
        blueFoodX = r * DOT_SIZE;
        int y = (int) (Math.random() * ((HEIGHT - DOT_SIZE) / DOT_SIZE));
        blueFoodY = y * DOT_SIZE;

        while (blueFoodX == foodX && blueFoodY == foodY) {
            r = (int) (Math.random() * ((WIDTH - DOT_SIZE) / DOT_SIZE));
            blueFoodX = r * DOT_SIZE;
            y = (int) (Math.random() * ((HEIGHT - DOT_SIZE) / DOT_SIZE));
            blueFoodY = y * DOT_SIZE;
        }
    }

    private void gameUpdate() {
        if (running) {
            move();
            checkFood();
            checkBlueFood();
            checkCollisions();
        }
        gamePanel.repaint();
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        int delay = 60;
        if (speedUpTimer > 0) {
            delay = 30;
            speedUpTimer--;
        }

        switch (direction) {
            case 'U': y[0] -= DOT_SIZE; break;
            case 'D': y[0] += DOT_SIZE; break;
            case 'L': x[0] -= DOT_SIZE; break;
            case 'R': x[0] += DOT_SIZE; break;
        }

        timer.setDelay(delay);
    }

    private void checkFood() {
        if ((x[0] == foodX) && (y[0] == foodY)) {
            bodyParts++;
            fruitsEaten++;
            placeFood();
            updateHighScore();
        }
    }

    private void checkBlueFood() {
        if ((x[0] == blueFoodX) && (y[0] == blueFoodY)) {
            bodyParts += 3;
            fruitsEaten += 3;
            placeBlueFoodRandomly();
            updateHighScore();
            speedUpTimer = 100;
        }
    }

    private void updateHighScore() {
        if (fruitsEaten > highScore) {
            highScore = fruitsEaten;
        }
    }

    private void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }

        if (y[0] < 0 || y[0] >= HEIGHT || x[0] < 0 || x[0] >= WIDTH) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    private void restartGame() {
        bodyParts = 3;
        fruitsEaten = 0;
        direction = 'R';
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 50 - i * 10;
            y[i] = 50;
        }
        placeFood();
        placeBlueFoodRandomly();
        timer.start();
        running = true;
        speedUpTimer = 0;
    }

    private void doDrawing(Graphics g) {
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        if (running) {
            g.setColor(Color.red);
            g.fillRect(foodX, foodY, DOT_SIZE, DOT_SIZE);

            g.setColor(Color.blue);
            g.fillRect(blueFoodX, blueFoodY, DOT_SIZE, DOT_SIZE);

            // Define the rainbow colors
            Color[] rainbowColors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.magenta};

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], DOT_SIZE, DOT_SIZE);
                } else {
                    // Set the color based on the body part index and the rainbow colors array
                    int colorIndex = (i - 1) % rainbowColors.length;
                    g.setColor(rainbowColors[colorIndex]);
                    g.fillRect(x[i], y[i], DOT_SIZE, DOT_SIZE);
                }
            }
        } else {
            gameOver(g);
        }
    }

    private void showScore(Graphics g) {
        g.setColor(Color.black);
        g.drawString("Fruits eaten: " + fruitsEaten, 10, 20);
        g.drawString("High Score: " + highScore, 10, 40);
    }

    private void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.drawString("Game Over", WIDTH / 2 - 50, HEIGHT / 2);
        g.drawString("Fruits eaten: " + fruitsEaten, WIDTH / 2 - 50, HEIGHT / 2 + 20);
        g.drawString("High Score: " + highScore, WIDTH / 2 - 50, HEIGHT / 2 + 40);
        g.drawString("Press r to restart", WIDTH / 2 - 50, HEIGHT / 2 + 60);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new SnakeGame();
            frame.setVisible(true);
        });
    }
}