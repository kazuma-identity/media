import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel {
  private List<Enemy> enemies;
  private List<Tower> towers;
  private long lastUpdateTime;
  private Timer timer;
  private boolean gameOver = false;

  public GamePanel(int width, int height) {
    setPreferredSize(new Dimension(width, height));
    enemies = new ArrayList<>();
    towers = new ArrayList<>();

    // 初期配置
    towers.add(new Tower(200, 300, 100, 5));
    enemies.add(new Enemy(0, 300, 1, 20));
    enemies.add(new Enemy(0, 320, 1, 30));

    lastUpdateTime = System.nanoTime();

    // キーリスナー追加
    setFocusable(true);
    requestFocusInWindow();
    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        handleKeyPressed(e);
      }
    });
  }

  public void start() {
    // 約60fps相当 (16msごとに更新)
    timer = new Timer(16, (ActionEvent e) -> gameLoop());
    timer.start();
  }

  private void gameLoop() {
    if (gameOver) {
      return;
    }

    long now = System.nanoTime();
    double deltaTime = (now - lastUpdateTime) / 1_000_000_000.0;
    lastUpdateTime = now;

    updateGame(deltaTime);
    repaint();
  }

  private void updateGame(double deltaTime) {
    // 敵を移動
    for (Enemy e : enemies) {
      e.update(deltaTime);
    }

    // タワー攻撃判定と範囲縮小
    Iterator<Tower> towerIt = towers.iterator();
    while (towerIt.hasNext()) {
      Tower t = towerIt.next();
      boolean towerCollapsed = false;

      for (Enemy e : enemies) {
        double dist = Math.sqrt(Math.pow(e.getX() - t.getX(), 2) + Math.pow(e.getY() - t.getY(), 2));
        if (dist <= t.getRange()) {
          // 敵にダメージを与える
          e.damage(t.getDamage());
          // 範囲を1縮める
          double newRange = t.getRange() - 1;
          t.setRange(newRange);

          // 範囲が0以下になったらタワー崩壊
          if (t.getRange() <= 0) {
            towerCollapsed = true;
            break; // このタワーは崩壊するので、敵への攻撃はループ終了
          }
        }
      }

      if (towerCollapsed) {
        towerIt.remove();
      }
    }

    // HPが0以下の敵を削除
    Iterator<Enemy> enemyIt = enemies.iterator();
    while (enemyIt.hasNext()) {
      Enemy e = enemyIt.next();
      if (e.getHp() <= 0) {
        enemyIt.remove();
      }
    }

    // 全タワーが無くなったらゲームオーバー
    if (towers.isEmpty()) {
      gameOver = true;
      if (timer != null) {
        timer.stop();
      }
      System.out.println("タワーがすべて崩壊！ゲームオーバー！");
    }
  }

  private void handleKeyPressed(KeyEvent e) {
    // 'E'キーで新たな敵を追加
    if (e.getKeyCode() == KeyEvent.VK_E) {
      enemies.add(new Enemy(0, 300, 1, 25));
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // 背景
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, getWidth(), getHeight());

    // 敵描画(赤い円)
    g.setColor(Color.RED);
    for (Enemy e : enemies) {
      g.fillOval((int) e.getX() - 5, (int) e.getY() - 5, 10, 10);
    }

    // タワー描画(青い四角)
    g.setColor(Color.BLUE);
    for (Tower t : towers) {
      g.fillRect((int) t.getX() - 10, (int) t.getY() - 10, 20, 20);

      // 攻撃範囲(黄色い円)
      g.setColor(Color.YELLOW);
      int range = (int) t.getRange();
      g.drawOval((int) (t.getX() - range), (int) (t.getY() - range), range * 2, range * 2);
      g.setColor(Color.BLUE);
    }

    // ゲームオーバー表示
    if (gameOver) {
      g.setColor(Color.WHITE);
      g.drawString("Game Over! タワーがすべて崩壊しました", getWidth() / 2 - 80, getHeight() / 2);
    }
  }
}
