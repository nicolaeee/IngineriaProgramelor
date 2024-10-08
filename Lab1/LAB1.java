package lab1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class Lab1 extends JPanel implements ActionListener, MouseListener {

    private ArrayList<Forma> forme = new ArrayList<>();
    private Timer timer;
    private int scor = 0;
    private int timpRamas = 60;
    private long ultimaActualizare;
    private Random rand = new Random();
    private boolean jocInDesfasurare = false;

    private JButton startButton;
    private JButton stopButton;
    private JButton restartButton;

    public Lab1() {
        setLayout(new BorderLayout());

        // Creăm butoanele
        startButton = new JButton("Play");
        stopButton = new JButton("Stop");
        restartButton = new JButton("Restart");

        // Adăugăm butoanele într-un panou la sud (jos)
        JPanel butoanePanou = new JPanel();
        butoanePanou.add(startButton);
        butoanePanou.add(stopButton);
        butoanePanou.add(restartButton);
        add(butoanePanou, BorderLayout.SOUTH);

        // Adăugăm acțiunile pentru butoane
        startButton.addActionListener(e -> startGame());
        stopButton.addActionListener(e -> stopGame());
        restartButton.addActionListener(e -> restartGame());

        // Inițializare joc
        timer = new Timer(50, this); // Actualizare la fiecare 50ms
        addMouseListener(this);
        ultimaActualizare = System.currentTimeMillis();
    }

    private void startGame() {
        if (!jocInDesfasurare) {
            jocInDesfasurare = true;
            timer.start();
        }
    }

    private void stopGame() {
        jocInDesfasurare = false;
        timer.stop();
    }

    private void restartGame() {
        scor = 0;
        timpRamas = 60;
        forme.clear();
        repaint();
        startGame();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Desenează toate formele
        for (Forma f : forme) {
            f.deseneaza(g);
        }

        // Desenează scorul și timpul rămas
        g.setColor(Color.BLACK);
        g.drawString("Scor: " + scor, 10, 20);
        g.drawString("Timp rămas: " + timpRamas + " secunde", 10, 40);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long acum = System.currentTimeMillis();

        if (jocInDesfasurare) {
            // Actualizează timpul rămas la fiecare secundă
            if (acum - ultimaActualizare >= 1000) {
                timpRamas--;
                ultimaActualizare = acum;

                // Adaugă o nouă formă o dată pe secundă
                if (timpRamas > 0) {
                    int x = rand.nextInt(getWidth() - 100);
                    int y = rand.nextInt(getHeight() - 100);
                    int dx = rand.nextInt(10) - 5;
                    int dy = rand.nextInt(10) - 5;
                    if (rand.nextBoolean()) {
                        forme.add(new Patrat(x, y, 50, dx, dy)); // Creează un patrat
                    } else {
                        forme.add(new Cerc(x, y, 50, dx, dy));   // Creează un cerc
                    }
                }

                // Oprește jocul când timpul expira
                if (timpRamas <= 0) {
                    stopGame();
                    JOptionPane.showMessageDialog(this, "Timpul a expirat! Scor final: " + scor);
                }
            }

            // Actualizează poziția fiecărei forme
            for (Forma f : forme) {
                f.misca(getWidth(), getHeight());
            }

            repaint(); // Redesenare fereastră
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (jocInDesfasurare) {
            for (int i = 0; i < forme.size(); i++) {
                Forma f = forme.get(i);
                if (f.continePunct(e.getX(), e.getY())) {
                    forme.remove(i); // Șterge forma pe care s-a dat click
                    scor++; // Crește scorul
                    repaint();
                    break;
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Joc cu forme mobile");
        Lab1 joc = new Lab1();
        frame.add(joc);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Clase pentru forme

    abstract class Forma {
        int x, y, dim, dx, dy;

        Forma(int x, int y, int dim, int dx, int dy) {
            this.x = x;
            this.y = y;
            this.dim = dim;
            this.dx = dx;
            this.dy = dy;
        }

        abstract void deseneaza(Graphics g);

        abstract boolean continePunct(int px, int py);

        void misca(int width, int height) {
            x += dx;
            y += dy;

            if (x < 0 || x + dim > width) {
                dx = -dx;
            }
            if (y < 0 || y + dim > height) {
                dy = -dy;
            }
        }
    }

    // Patrat
    class Patrat extends Forma {
        Patrat(int x, int y, int dim, int dx, int dy) {
            super(x, y, dim, dx, dy);
        }

        @Override
        void deseneaza(Graphics g) {
            g.setColor(Color.BLUE);
            g.fillRect(x, y, dim, dim);
        }

        @Override
        boolean continePunct(int px, int py) {
            return px >= x && px <= x + dim && py >= y && py <= y + dim;
        }
    }

    // Cerc
    class Cerc extends Forma {
        Cerc(int x, int y, int dim, int dx, int dy) {
            super(x, y, dim, dx, dy);
        }

        @Override
        void deseneaza(Graphics g) {
            g.setColor(Color.RED);
            g.fillOval(x, y, dim, dim);
        }

        @Override
        boolean continePunct(int px, int py) {
            int centerX = x + dim / 2;
            int centerY = y + dim / 2;
            return Math.sqrt(Math.pow(px - centerX, 2) + Math.pow(py - centerY, 2)) <= dim / 2;
        }
    }
}
