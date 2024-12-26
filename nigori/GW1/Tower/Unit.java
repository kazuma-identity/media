import java.awt.Graphics;

public abstract class Unit {
  protected double x, y;
  protected double speed;
  protected int hp;
  protected int level;
  protected int cost;
  protected UnitType type;
  protected Player owner; // ユニットの所有者

  public Unit(double x, double y, double speed, int cost, UnitType type, Player owner) {
    this.x = x;
    this.y = y;
    this.speed = speed;
    this.cost = cost;
    this.type = type;
    this.owner = owner;
    this.level = 1;
    this.hp = (type == UnitType.DEFENSE) ? 100 : 150;
  }

  public abstract void update(double deltaTime, Game game);

  public abstract void draw(Graphics g);

  // ゲッター
  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public int getHp() {
    return hp;
  }

  public UnitType getType() {
    return type;
  }

  public Player getOwner() {
    return owner;
  }

  public int getLevel() {
    return level;
  }

  // ダメージを受ける
  public void damage(int amount) {
    hp -= amount;
  }

  public void upgrade() {
    level++;
    hp += (type == UnitType.DEFENSE) ? 50 : 75;
  }
}
