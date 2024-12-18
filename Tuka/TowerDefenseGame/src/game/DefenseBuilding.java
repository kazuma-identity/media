// DefenseBuilding --- 一定範囲内の敵ユニットを攻撃する

package game;

import java.awt.Graphics;
import java.awt.Color;

public class DefenseBuilding extends Building {
    public DefenseBuilding(double x, double y) {
        super(x, y, 20, 300);
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
        
        // HPバーの描画
        g.setColor(Color.RED);
        g.fillRect((int)x - 8, (int)y - 12, 16, 3);
        g.setColor(Color.GREEN);
        int hpBarWidth = (int)((health / 100.0) * 16);
        g.fillRect((int)x - 8, (int)y - 12, hpBarWidth, 3);
    }
}