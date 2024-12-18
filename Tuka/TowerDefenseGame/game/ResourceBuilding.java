package media.Tuka.TowerDefenseGame;

import java.awt.Color;
import java.awt.Graphics;

public class ResourceBuilding extends Building {
    public ResourceBuilding(double x, double y) {
        super(x, y, 100);
    }

    @Override
    public void update(double deltaTime) {
        // クライアント側では特に処理なし
    }

    @Override
    public void draw(Graphics g) {
        // 資源設備の描画（緑の丸）
        g.setColor(Color.GREEN);
        g.fillOval((int)x - 15, (int)y - 15, 30, 30);

        // レベル表示
        g.setColor(Color.WHITE);
        g.drawString("Lv " + level, (int)x - 10, (int)y + 5);
    }
}