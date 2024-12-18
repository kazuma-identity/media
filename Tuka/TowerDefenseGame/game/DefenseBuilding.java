package media.Tuka.TowerDefenseGame;
// package game;

import java.awt.Graphics;
import java.awt.Color;

public class DefenseBuilding extends Building {
    public DefenseBuilding(double x, double y) {
        super(x, y, 150);
    }

    @Override
    public void update(double deltaTime) {
        // クライアント側では特に処理なし
    }

    @Override
    public void draw(Graphics g) {
        // 防衛設備の描画（赤の四角）
        g.setColor(Color.RED);
        g.fillRect((int)x - 15, (int)y - 15, 30, 30);

        // レベル表示
        g.setColor(Color.WHITE);
        g.drawString("Lv " + level, (int)x - 10, (int)y + 5);
    }
}