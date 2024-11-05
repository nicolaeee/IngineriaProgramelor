import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SimpleGraphicEditor extends JPanel {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int SHAPE_SIZE = 50;

    private ArrayList<Shape> shapes;
    private Shape selectedShape;
    private Point initialClick;
    private Color currentColor;
    private Random random;

    public SimpleGraphicEditor() {
        shapes = new ArrayList<>();
        selectedShape = null;
        currentColor = Color.BLUE; // Culoarea inițială
        random = new Random();

        // Adaugare butoane
        JButton addCircleButton = new JButton("Adaugă Cerc");
        JButton addSquareButton = new JButton("Adaugă Pătrat");
        JButton deleteShapeButton = new JButton("Șterge Forma");
        JButton colorButton = new JButton("Selectează Culoare");

        addCircleButton.addActionListener(e -> addShape("circle"));
        addSquareButton.addActionListener(e -> addShape("square"));
        deleteShapeButton.addActionListener(e -> deleteShape());
        colorButton.addActionListener(e -> selectColor());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addCircleButton);
        buttonPanel.add(addSquareButton);
        buttonPanel.add(deleteShapeButton);
        buttonPanel.add(colorButton);

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.SOUTH);

        // Gestionare drag-and-drop
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectShapeAt(e.getX(), e.getY());
                initialClick = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                initialClick = null; // Resetează punctul de click inițial
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedShape != null && initialClick != null) {
                    int deltaX = e.getX() - initialClick.x;
                    int deltaY = e.getY() - initialClick.y;
                    selectedShape.moveBy(deltaX, deltaY);
                    initialClick = e.getPoint();
                    repaint();
                }
            }
        });
    }

    private void addShape(String shapeType) {
        int x = random.nextInt(WINDOW_WIDTH - SHAPE_SIZE);
        int y = random.nextInt(WINDOW_HEIGHT - SHAPE_SIZE);

        Shape newShape = "circle".equals(shapeType) ? new Circle(x, y, SHAPE_SIZE, currentColor) : new Square(x, y, SHAPE_SIZE, currentColor);
        shapes.add(newShape);
        repaint();
    }

    private void deleteShape() {
        if (selectedShape != null) {
            shapes.remove(selectedShape);  // Elimină forma selectată din listă
            selectedShape = null;  // Resetează forma selectată
            repaint();
        }
    }

    private void selectColor() {
        Color chosenColor = JColorChooser.showDialog(this, "Alege o Culoare", currentColor);
        if (chosenColor != null) {
            currentColor = chosenColor;
        }
    }

    private void selectShapeAt(int x, int y) {
        selectedShape = null;
        for (Shape shape : shapes) {
            if (shape.contains(x, y)) {
                selectedShape = shape;
                break;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Shape shape : shapes) {
            shape.draw(g);
            if (shape == selectedShape) {
                g.setColor(Color.RED);
                g.drawRect(shape.getX() - 2, shape.getY() - 2, SHAPE_SIZE + 4, SHAPE_SIZE + 4);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Editor Grafic Simplu");
        SimpleGraphicEditor editor = new SimpleGraphicEditor();
        frame.add(editor);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Clasele pentru cerc și pătrat
    abstract class Shape {
        int x, y, size;
        Color color;

        public Shape(int x, int y, int size, Color color) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.color = color;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void moveBy(int deltaX, int deltaY) {
            this.x += deltaX;
            this.y += deltaY;
        }

        public abstract void draw(Graphics g);

        public abstract boolean contains(int x, int y);
    }

    class Circle extends Shape {
        public Circle(int x, int y, int size, Color color) {
            super(x, y, size, color);
        }

        @Override
        public void draw(Graphics g) {
            g.setColor(color);
            g.fillOval(x, y, size, size);
        }

        @Override
        public boolean contains(int x, int y) {
            int centerX = this.x + size / 2;
            int centerY = this.y + size / 2;
            int radius = size / 2;
            return (Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2)) <= Math.pow(radius, 2);
        }
    }

    class Square extends Shape {
        public Square(int x, int y, int size, Color color) {
            super(x, y, size, color);
        }

        @Override
        public void draw(Graphics g) {
            g.setColor(color);
            g.fillRect(x, y, size, size);
        }

        @Override
        public boolean contains(int x, int y) {
            return x >= this.x && x <= this.x + size && y >= this.y && y <= this.y + size;
        }
    }
}
