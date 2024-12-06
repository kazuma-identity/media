public class Unit {
    private int x, y;
    private int attackPower;

    public Unit(int x, int y, int attackPower) {
        this.x = x;
        this.y = y;
        this.attackPower = attackPower;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void attack(Castle castle) {
        if (x == castle.getX() && y == castle.getY()) {
            castle.takeDamage(attackPower);
        } else {
            System.out.println("城に届きませんでした！");
        }
    }
}
