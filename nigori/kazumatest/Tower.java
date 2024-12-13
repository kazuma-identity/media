public class Tower {
  private double x;
  private double y;
  private double range;
  private int damage;

  public Tower(double x, double y, double range, int damage) {
    this.x = x;
    this.y = y;
    this.range = range;
    this.damage = damage;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getRange() {
    return range;
  }

  public int getDamage() {
    return damage;
  }

  public void setRange(double newRange) {
    this.range = newRange;
  }
}
