public class Enemy {
  private double x;
  private double y;
  private double speed;
  private int hp;

  public Enemy(double x, double y, double speed, int hp) {
    this.x = x;
    this.y = y;
    this.speed = speed;
    this.hp = hp;
  }

  public void update(double deltaTime) {
    // 単純に右方向へ移動 (1秒あたりspeedピクセル進む前提)
    x += speed * (deltaTime * 60);
  }

  public void damage(int amount) {
    hp -= amount;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public int getHp() {
    return hp;
  }
}
