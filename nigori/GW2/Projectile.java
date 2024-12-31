import java.awt.Graphics;
import java.awt.Color;

public class Projectile {
  private double x, y;
  private double speed;
  private double directionX, directionY;
  private int damage;
  private boolean active;

  public Projectile(double x, double y, double targetX, double targetY, double speed, int damage) {
    this.x = x;
    this.y = y;
    this.speed = speed;
    double dx = targetX - x;
    double dy = targetY - y;
    double distance = Math.hypot(dx, dy);
    if (distance == 0) {
      this.directionX = 0;
      this.directionY = 0;
    } else {
      this.directionX = dx / distance;
      this.directionY = dy / distance;
    }
    this.damage = damage;
    this.active = true;
  }

  public void update(double deltaTime, Game game) {
    x += directionX * speed * deltaTime;
    y += directionY * speed * deltaTime;

    // 攻城ユニットにのみダメージを与える
    for (Unit unit : game.getUnits()) {
      if (unit.getType() == UnitType.SIEGE) { // 攻城ユニットのみ対象
        double distance = Math.hypot(x - unit.getX(), y - unit.getY());
        if (distance <= 10) { // 当たり判定
          unit.damage(damage);
          active = false;
          if (unit.getHp() <= 0) {
            game.removeUnit(unit);
          }
          break;
        }
      }
    }

    // 画面外に出たら無効化
    if (x < 0 || x > 800 || y < 0 || y > 600) { // 画面サイズに合わせて調整
      active = false;
    }
  }

  public void draw(Graphics g) {
    if (!active)
      return;
    // プロジェクタイルを小さな白い円として描画
    g.setColor(Color.WHITE);
    g.fillOval((int) x - 3, (int) y - 3, 6, 6);
  }

  public boolean isActive() {
    return active;
  }

  public void deactivate() {
    active = false;
  }

  // シリアライズメソッド
  public String serialize() {
    return x + "," + y + "," + speed + "," + directionX + "," + directionY + "," + damage + "," + active;
  }

  // デシリアライズ用コンストラクタ
  public Projectile(String data) {
    String[] fields = data.split(",");
    this.x = Double.parseDouble(fields[0]);
    this.y = Double.parseDouble(fields[1]);
    this.speed = Double.parseDouble(fields[2]);
    this.directionX = Double.parseDouble(fields[3]);
    this.directionY = Double.parseDouble(fields[4]);
    this.damage = Integer.parseInt(fields[5]);
    this.active = Boolean.parseBoolean(fields[6]);
  }
}
