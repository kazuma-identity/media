import java.awt.Graphics;
import java.awt.Color;

public class DefenseUnit extends Unit {
  private double attackRange;
  private int damage;
  private double attackCooldown;
  private double timeSinceLastAttack;

  public DefenseUnit(double x, double y, Player owner) {
    super(x, y, 0, 50, UnitType.DEFENSE, owner);
    this.attackRange = 80;
    this.damage = 30;
    this.attackCooldown = 1.5; // 攻撃間隔（秒）
    this.timeSinceLastAttack = 0;
  }

  @Override
  public void update(double deltaTime, Game game) {
    timeSinceLastAttack += deltaTime;
    if (timeSinceLastAttack >= attackCooldown) {
      // 攻撃ロジック（防衛ユニットは特定の行動を持たない場合が多いため、ここでは何もしません）
      timeSinceLastAttack = 0;
    }
  }

  @Override
  public void draw(Graphics g) {
    // 防衛ユニットの描画（例: 青の丸）
    g.setColor(Color.BLUE);
    g.fillOval((int) x - 8, (int) y - 8, 16, 16);

    // HPバーの描画
    g.setColor(Color.RED);
    g.fillRect((int) x - 8, (int) y - 12, 16, 3);
    g.setColor(Color.GREEN);
    int hpBarWidth = (int) ((hp / 100.0) * 16); // 最大HPを100と仮定
    g.fillRect((int) x - 8, (int) y - 12, hpBarWidth, 3);
  }
}
