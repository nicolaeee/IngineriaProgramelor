import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class BeeGame extends JPanel {
    private ArrayList<Bee> albine = new ArrayList<>();
    private Random rand = new Random();

    public BeeGame() {
        setBackground(Color.WHITE);
        int numarAlbine = 10; // Numărul de albine

        // Creăm albine și pornim firele de execuție
        for (int i = 0; i < numarAlbine; i++) {
            Bee albina = new Bee(rand.nextInt(600), rand.nextInt(400));
            albine.add(albina);
            Thread thread = new Thread(albina);
            thread.start();
        }

        // Timer pentru a redesena fereastra la fiecare 50 ms
        Timer timer = new Timer(50, e -> repaint());
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Desenează fiecare albina
        for (Bee albina : albine) {
            albina.deseneaza(g);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Bee Game - Zborul albinelor");
        BeeGame game = new BeeGame();
        frame.add(game);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Clasa pentru albina, care implementează Runnable pentru a folosi thread-uri
    class Bee implements Runnable {
        private int x, y;
        private int dimensiune = 30;
        private int dx, dy;
        private Random rand = new Random();

        public Bee(int x, int y) {
            this.x = x;
            this.y = y;
            this.dx = rand.nextInt(7) - 3; // Viteză inițială aleatorie pe X
            this.dy = rand.nextInt(7) - 3; // Viteză inițială aleatorie pe Y
        }

        public void deseneaza(Graphics g) {
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, dimensiune, dimensiune); // Desenează pătratul galben (albina)
        }

        @Override
        public void run() {
            while (true) {
                // Actualizează poziția albinaei
                x += dx;
                y += dy;

                // Dacă albina ajunge la marginea ferestrei, schimbă direcția
                if (x < 0 || x + dimensiune > getWidth()) {
                    dx = -dx; // Schimbă direcția pe X
                }
                if (y < 0 || y + dimensiune > getHeight()) {
                    dy = -dy; // Schimbă direcția pe Y
                }

                // Ocazional, schimbă direcția în mod haotic
                if (rand.nextInt(100) < 5) {
                    dx = rand.nextInt(7) - 3;
                    dy = rand.nextInt(7) - 3;
                }

                // Dormi pentru 50 de milisecunde pentru a controla viteza
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

