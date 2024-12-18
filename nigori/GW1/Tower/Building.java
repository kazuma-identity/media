import java.awt.Graphics;

public abstract class Building {
  protected double x, y;
  protected int level;
  protected int hp;
  protected int cost;
  protected Player owner; // 建物の所有者

  public Building(double x, double y, int cost, Player owner) {
    this.x = x;
    this.y = y;
    this.cost = cost;
    this.owner = owner;
    this.level = 1;
    this.hp = 500;
  }

  public abstract void update(double deltaTime, Game game);

  public abstract void draw(Graphics g);

  public void upgrade() {
    level++;
    hp += 200;
  }

  // ゲッター
  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public int getLevel() {
    return level;
  }

  public int getHp() {
    return hp;
  }

  public int getCost() {
    return cost;
  }

  public Player getOwner() {
    return owner;
  }

  // ダメージを受ける
  public void damage(int amount) {
    hp -= amount;
  }
}
