import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// --- Figure クラスとその派生クラス ---
class Figure {
    protected int x, y, width, height;
    protected Color color;

    public Figure(int x, int y, int w, int h, Color c) {
        this.x = x;
        this.y = y;
        width = w;
        height = h;
        color = c;
    }

    public void setSize(int w, int h) {
        width = w;
        height = h;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void reshape(int x1, int y1, int x2, int y2) {
        int newx = Math.min(x1, x2);
        int newy = Math.min(y1, y2);
        int neww = Math.abs(x1 - x2);
        int newh = Math.abs(y1 - y2);
        setLocation(newx, newy);
        setSize(neww, newh);
    }

    public void draw(Graphics g) {}
}

class RectangleFigure extends Figure {
    public RectangleFigure(int x, int y, int w, int h, Color c) {
        super(x, y, w, h, c);
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.drawRect(x, y, width, height);
    }
}

class FillRectFigure extends Figure {
    public FillRectFigure(int x, int y, int w, int h, Color c) {
        super(x, y, w, h, c);
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }
}

// --- Model ---
class DrawModel extends Observable {
    protected ArrayList<Figure> fig;
    protected Figure drawingFigure;
    protected Color currentColor;
    protected boolean Castle;

    public DrawModel() {
        fig = new ArrayList<>();
        drawingFigure = null;
        currentColor = Color.red;
        Castle = false;
    }

    public void setCurrentColor(Color color) {
        currentColor = color;
        setChanged();
        notifyObservers();
    }

    public ArrayList<Figure> getFigures() {
        return fig;
    }

    public void setCastle(boolean castle) {
        Castle = castle;
    }

    public void createFigure(int x, int y) {
        Figure f;
        if (!Castle) {
            f = new FillRectFigure(x, y, 0, 0, currentColor);
        } else {
            f = new RectangleFigure(x, y, 0, 0, currentColor);
        }
        fig.add(f);
        drawingFigure = f;
        setChanged();
        notifyObservers();
    }

    public void reshapeFigure(int x1, int y1, int x2, int y2) {
        if (drawingFigure != null) {
            drawingFigure.reshape(x1, y1, x2, y2);
            setChanged();
            notifyObservers();
        }
    }
}

// --- View ---
class ViewPanel extends JPanel implements Observer {
    protected DrawModel model;

    public ViewPanel(DrawModel m, DrawController c) {
        this.setBackground(Color.white);
        this.addMouseListener(c);
        this.addMouseMotionListener(c);
        this.addKeyListener(c);
        this.setFocusable(true);
        model = m;
        model.addObserver(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Figure f : model.getFigures()) {
            f.draw(g);
        }
    }

    public void update(Observable o, Object arg) {
        repaint();
    }
}

// --- Controller ---
class DrawController implements MouseListener, MouseMotionListener, KeyListener {
    protected DrawModel model;
    protected int pushStartX, pushStartY;

    public DrawController(DrawModel a) {
        model = a;
    }

    public void keyPressed(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

    public void mouseClicked(MouseEvent e) {
        pushStartX = e.getX();
        pushStartY = e.getY();
        model.createFigure(pushStartX, pushStartY);
        model.reshapeFigure(pushStartX, pushStartY, pushStartX + 50, pushStartY + 50);
        model.setCastle(true);
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}

// --- Main Frame ---
class DrawFrame extends JFrame {
    CardLayout cardLayout;
    JPanel mainPanel;

    public DrawFrame() {
        // 全体のレイアウト
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // モデルとビュー
        DrawModel model = new DrawModel();
        DrawController cont = new DrawController(model);

        // スクリーン① (タイトル画面)
        JPanel screen1 = new JPanel();
        screen1.setLayout(new BorderLayout());
        JLabel title = new JLabel("Tower Deffence", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        JButton startButton = new JButton("START");
        startButton.addActionListener(e -> cardLayout.show(mainPanel, "Screen2"));

        screen1.add(title, BorderLayout.CENTER);
        screen1.add(startButton, BorderLayout.SOUTH);

        // スクリーン② (描画画面)
        ViewPanel screen2 = new ViewPanel(model, cont);

        // スクリーンをカードとして追加
        mainPanel.add(screen1, "Screen1");
        mainPanel.add(screen2, "Screen2");

        // 初期設定
        this.add(mainPanel);
        this.setTitle("Tower Deffence");
        this.setSize(500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new DrawFrame();
    }
}
