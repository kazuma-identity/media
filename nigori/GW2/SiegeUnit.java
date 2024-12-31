import java.awt.Graphics;
import java.awt.Color;

public class SiegeUnit extends Unit {

  private double attackRange;
  private int damage;
  private double attackCooldown;
  private double timeSinceLastAttack;
  private double targetX, targetY;

  public SiegeUnit(double x, double y, double speed, int cost, Player owner) {
    super(x, y, speed, cost, UnitType.SIEGE, owner);
    this.attackRange = 120;
    this.damage = 50;
    this.attackCooldown = 5.0; // 攻撃間隔（秒）
    this.timeSinceLastAttack = 0;
    this.targetX = x; // 初期ターゲットは相手の城
    this.targetY = y;
    this.speed = 50; // 移動速度
  }

  @Override
  public void update(double deltaTime, Game game) {
    // 相手の城に向かって移動
    Player opponent = game.getOpponent(this);
    if (opponent == null)
      return;

    targetX = opponent.getCastle().getX();
    targetY = opponent.getCastle().getY();

    double dx = targetX - x;
    double dy = targetY - y;
    double distance = Math.hypot(dx, dy);
    if (distance > speed * deltaTime) {
      x += (dx / distance) * speed * deltaTime;
      y += (dy / distance) * speed * deltaTime;
    } else {
      x = targetX;
      y = targetY;
    }

    // 城に到達した場合のみ攻撃を行う
    if (x == targetX && y == targetY) {
      timeSinceLastAttack += deltaTime;
      if (timeSinceLastAttack >= attackCooldown) {
        // 城を攻撃
        opponent.getCastle().damage(damage);
        timeSinceLastAttack = 0;
      }
    }
  }

  @Override
  public void draw(Graphics g) {
    // 攻城ユニットの描画（例: オレンジの丸）
    g.setColor(Color.ORANGE);
    g.fillOval((int) x - 10, (int) y - 10, 20, 20);

    // HPバーの描画
    g.setColor(Color.RED);
    g.fillRect((int) x - 10, (int) y - 15, 20, 4);
    g.setColor(Color.GREEN);
    int hpBarWidth = (int) ((hp / 150.0) * 20); // 最大HPを150と仮定
    g.fillRect((int) x - 10, (int) y - 15, hpBarWidth, 4);
  }
}
