package game;

import java.awt.Graphics;

public abstract class Building implements GameObject {
    protected double x, y;
    protected int level;
    protected int health;
    protected int cost;

    public Building(double x, double y, int cost, int health) {
        this.x = x;
        this.y = y;
        this.cost = cost;
        this.health = health;
        this.level = 1;
    }

    // ゲッターとセッター
    public double getX() { return x; }
    public double getY() { return y; }
    public int getLevel() { return level; }
    public void upgrade() { level++; }
    public int gethealth() { return health; }
    public int getCost() { return cost; }

    // 更新と描画の抽象メソッド
    @Override
    public abstract void update(double deltaTime);
    @Override
    public abstract void draw(Graphics g);
}