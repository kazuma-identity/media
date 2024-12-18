import javax.swing.*;
import java.awt.*;

public class FontTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame("日本語フォントテスト");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setFont(new Font("Noto Sans CJK JP", Font.PLAIN, 16));
                g.setColor(Color.BLACK);
                g.drawString("こんにちは、世界！", 50, 100);
            }
        };

        frame.add(panel);
        frame.setVisible(true);
    }
}
