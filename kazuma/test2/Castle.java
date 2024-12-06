public class Castle {
    private int x, y;
    private int health;

    public Castle(int x, int y, int health) {
        this.x = x;
        this.y = y;
        this.health = health;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHealth() {
        return health;
    }

    public void takeDamage(int damage) {
        health -= damage;
        System.out.println("城が攻撃されました！ 残りHP: " + Math.max(0, health));
    }

    public boolean isDestroyed() {
        return health <= 0;
    }
}
