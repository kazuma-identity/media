import java.awt.Graphics;
import java.awt.Color;

public class ResourceBuilding extends Building {

  public ResourceBuilding(double x, double y, Player owner) {
    super(x, y, 100, owner);
    // 資源生成関連の変数は不要
  }

  @Override
  public void update(double deltaTime, Game game) {
    // 資源生成はGameクラスで管理するため、ここでは何もしません
  }

  @Override
  public void draw(Graphics g) {
    // 資源設備の描画（例: 緑の丸）
    g.setColor(Color.GREEN);
    g.fillOval((int) x - 15, (int) y - 15, 30, 30);

    // レベル表示
    g.setColor(Color.WHITE);
    g.drawString("Lv " + level, (int) x - 10, (int) y + 5);

    // 資源施設固有の情報があればここに追加
  }
}
