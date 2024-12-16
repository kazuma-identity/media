import java.awt.Graphics;
import java.awt.Color;

public class DefenseBuilding extends Building {
  protected double attackRange;
  protected int damage;
  protected double attackCooldown;
  protected double timeSinceLastAttack;

  public DefenseBuilding(double x, double y, Player owner) {
    super(x, y, 150, owner);
    this.attackRange = 150; // 攻撃範囲を拡大
    this.damage = 30; // プロジェクタイルのダメージ
    this.attackCooldown = 1.5; // 攻撃間隔（秒）
    this.timeSinceLastAttack = 0;
  }

  @Override
  public void update(double deltaTime, Game game) {
    timeSinceLastAttack += deltaTime;
    if (timeSinceLastAttack >= attackCooldown) {
      // 敵ユニットを探し、攻撃範囲内の敵にプロジェクタイルを発射する
      for (Unit unit : game.getUnits()) {
        if (unit.getType() == UnitType.SIEGE && unit.getOwner() != this.owner) { // 攻城ユニットかつ敵
          // 敵ユニットが攻撃範囲内かつ陣地に侵入しているかを確認
          double distance = Math.hypot(x - unit.getX(), y - unit.getY());
          if (distance <= attackRange && game.isWithinTerritory(owner, unit.getX(), unit.getY())) {
            // プロジェクタイルを発射
            Projectile p = new Projectile(x, y, unit.getX(), unit.getY(), 300, damage);
            game.addProjectile(p);
            timeSinceLastAttack = 0;
            break; // 一度に一つのユニットに対してのみ発射
          }
        }
      }
    }
  }

  @Override
  public void draw(Graphics g) {
    // 防衛設備の描画（例: 赤の四角）
    g.setColor(Color.RED);
    g.fillRect((int) x - 15, (int) y - 15, 30, 30);

    // 攻撃範囲の表示（オプション）
    g.setColor(new Color(255, 0, 0, 50)); // 半透明の赤
    g.fillOval((int) (x - attackRange), (int) (y - attackRange), (int) (attackRange * 2), (int) (attackRange * 2));

    // レベル表示
    g.setColor(Color.WHITE);
    g.drawString("Lv " + level, (int) x - 10, (int) y + 5);
  }
}
