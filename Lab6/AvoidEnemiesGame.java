import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class AvoidEnemiesGame extends JPanel implements ActionListener {
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 800;
    private static final int PLAYER_SIZE = 50;
    private static final int ENEMY_SIZE = 50;
    private static final int ENEMY_SPEED = 3;
    private static final int REGENERATION_INTERVAL = 10000;

    private Timer gameTimer;
    private Timer enemyTimer;
    private Timer countdownTimer;
    private Player player;
    private List<Enemy> enemies;
    private Random random;
    private boolean gameOver;
    private int countdown;

    public AvoidEnemiesGame() {
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        player = new Player();
        enemies = new ArrayList<>();
        random = new Random();
        gameOver = false;

        gameTimer = new Timer(30, this);
        gameTimer.start();

        enemyTimer = new Timer(REGENERATION_INTERVAL, e -> generateEnemies());
        enemyTimer.start();

        countdownTimer = new Timer(1000, e -> countdownTick());

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                player.keyPressed(e);
            }
        });

        generateEnemies();
    }

    private void generateEnemies() {
        if (gameOver) return;
        enemies.clear();

        for (int i = 0; i < 5; i++) {
            int x, y;
            boolean overlap;

            do {
                overlap = false;
                x = random.nextInt(WINDOW_WIDTH - ENEMY_SIZE);
                y = 0;

                for (Enemy enemy : enemies) {
                    if (Math.abs(enemy.getX() - x) < ENEMY_SIZE + 1) {
                        overlap = true;
                        break;
                    }
                }
            } while (overlap);

            enemies.add(new Enemy(x, y));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over", WINDOW_WIDTH / 2 - 100, WINDOW_HEIGHT / 2 - 30);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Restart in: " + countdown, WINDOW_WIDTH / 2 - 90, WINDOW_HEIGHT / 2 + 30);
        } else {
            player.draw(g);
            for (Enemy enemy : enemies) {
                enemy.draw(g);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            player.move();
            moveEnemies();
            checkCollisions();
            repaint();
        }
    }

    private void moveEnemies() {
        for (Iterator<Enemy> iterator = enemies.iterator(); iterator.hasNext(); ) {
            Enemy enemy = iterator.next();
            enemy.move();
            if (enemy.getY() > WINDOW_HEIGHT) {
                iterator.remove();
            }
        }
    }

    private void checkCollisions() {
        Rectangle playerBounds = player.getBounds();
        for (Enemy enemy : enemies) {
            if (playerBounds.intersects(enemy.getBounds())) {
                gameOver = true;
                gameTimer.stop();
                enemyTimer.stop();
                countdown = 3;  // Setăm timer-ul de numărătoare inversă la 3 secunde
                countdownTimer.start();
                break;
            }
        }
    }

    private void countdownTick() {
        countdown--;
        repaint();

        if (countdown <= 0) {
            countdownTimer.stop();
            resetGame();
        }
    }

    private void resetGame() {
        player = new Player();
        enemies.clear();
        generateEnemies();
        gameOver = false;

        gameTimer.start();
        enemyTimer.start();
        repaint();
    }

    private class Player {
        private int x;
        private int y;
        private int dx;
        private int dy;
        private Image playerImage;

        public Player() {
            x = WINDOW_WIDTH / 2 - PLAYER_SIZE / 2;
            y = WINDOW_HEIGHT - PLAYER_SIZE - 20;

            try {
                playerImage = ImageIO.read(new File("C:\\Users\\_ Legion 5 _\\Desktop\\LOL\\Facultate\\AN3\\Sem1\\IP-Tema 5\\bmw-image.png"));
            } catch (IOException e) {
                System.out.println("Eroare la încărcarea imaginii: " + e.getMessage());
            }
        }

        public void draw(Graphics g) {
            if (playerImage != null) {
                g.drawImage(playerImage, x, y, PLAYER_SIZE, PLAYER_SIZE, null);
            } else {
                g.setColor(Color.BLUE);
                g.fillRect(x, y, PLAYER_SIZE, PLAYER_SIZE);
            }
        }

        public void move() {
            x += dx;
            y += dy;

            if (x < 0) x = 0;
            if (x > WINDOW_WIDTH - PLAYER_SIZE) x = WINDOW_WIDTH - PLAYER_SIZE;
            if (y < 0) y = 0;
            if (y > WINDOW_HEIGHT - PLAYER_SIZE) y = WINDOW_HEIGHT - PLAYER_SIZE;
        }

        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_LEFT) {
                dx = -5;
            } else if (key == KeyEvent.VK_RIGHT) {
                dx = 5;
            } else if (key == KeyEvent.VK_UP) {
                dy = -5;
            } else if (key == KeyEvent.VK_DOWN) {
                dy = 5;
            }
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, PLAYER_SIZE, PLAYER_SIZE);
        }
    }

    private class Enemy {
        private int x;
        private int y;
        private Image enemyImage;

        public Enemy(int x, int y) {
            this.x = x;
            this.y = y;

            try {
                enemyImage = ImageIO.read(new File("C:\\Users\\_ Legion 5 _\\Desktop\\LOL\\Facultate\\AN3\\Sem1\\IP - Tema 6\\mercedes-logo.png"));
            } catch (IOException e) {
                System.out.println("Eroare la încărcarea imaginii inamicilor: " + e.getMessage());
            }
        }

        public void draw(Graphics g) {
            if (enemyImage != null) {
                g.drawImage(enemyImage, x, y, ENEMY_SIZE, ENEMY_SIZE, null);
            } else {
                g.setColor(Color.RED);
                g.fillRect(x, y, ENEMY_SIZE, ENEMY_SIZE);
            }
        }

        public void move() {
            y += ENEMY_SPEED;
        }

        public int getY() {
            return y;
        }

        public int getX() {
            return x;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, ENEMY_SIZE, ENEMY_SIZE);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Avoid Enemies Game");
        AvoidEnemiesGame game = new AvoidEnemiesGame();
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
