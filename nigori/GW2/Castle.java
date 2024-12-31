import java.awt.Graphics;
import java.awt.Color;

public class Castle {
  private double x, y;
  private int hp;
  private int size = 40; // 城のサイズ

  public Castle(double x, double y, int hp) {
    this.x = x;
    this.y = y;
    this.hp = hp;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public int getHp() {
    return hp;
  }

  public void damage(int amount) {
    hp -= amount;
  }

  public void draw(Graphics g, Color color) {
    // 城の描画（例: 指定された色の四角）
    g.setColor(color);
    g.fillRect((int) x - size / 2, (int) y - size / 2, size, size);

    // HPバーの描画
    g.setColor(Color.RED);
    g.fillRect((int) x - size / 2, (int) y - size / 2 - 10, size, 5);
    g.setColor(Color.GREEN);
    int hpBarWidth = (int) ((hp / 1000.0) * size); // 最大HPを1000と仮定
    g.fillRect((int) x - size / 2, (int) y - size / 2 - 10, hpBarWidth, 5);
  }
}
