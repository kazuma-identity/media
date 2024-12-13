
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import java.util.*;
import java.util.List;

// 描画した図形を記録する Figure class (継承して使用)
class Figure {
    protected int x, y, width, hight;
    protected Color color;
  
    public Figure(int x, int y, int w, int h, Color c) {
      this.x = x; this.y = y;
      width = h; hight = h;
      color = c;
    }
    public void setSize(int w, int h) {
      width = w; hight = h;
    }
    public void setLocation(int x, int y) {
      this.x = x; this.y = y;
    }
    public void reshape(int x, int y, int xsize, int ysize) {
      int newx = x - xsize/2;
      int newy = y - ysize/2;
      int neww = xsize;
      int newh = ysize;
      setLocation(newx, newy);
      setSize(neww, newh);
    }
    public void draw(Graphics h) {}
}

// GameModel: ゲーム全体の状態を管理する
class GameModel {
    private int playerResources;
    private int enemyResources;
    private Castle playerCastle;
    private Castle enemyCastle;
    private List<Unit> playerUnits;
    private List<Unit> enemyUnits;
    private List<Building> playerBuildings;
    private List<Building> enemyBuildings;

    public GameModel() {
        this.playerResources = 100;
        this.enemyResources = 100;
        this.playerCastle = new Castle(1000);
        this.enemyCastle = new Castle(1000);
        this.playerUnits = new ArrayList<>();
        this.enemyUnits = new ArrayList<>();
        this.playerBuildings = new ArrayList<>();
        this.enemyBuildings = new ArrayList<>();
    }

    public void increaseResources() {
        for (Building b : playerBuildings) {
            if (b instanceof ResourceBuilding) {
                playerResources += ((ResourceBuilding) b).generateResources();
            }
        }
        enemyResources += 10; // 敵の資源増加（AI）
    }

    public void updateGameState() {
        for (Unit unit : playerUnits) unit.move();
        for (Unit unit : enemyUnits) unit.move();

        for (Unit unit : playerUnits) {
            if (unit instanceof SiegeUnit && unit.reachedTarget(enemyCastle)) {
                enemyCastle.takeDamage(unit.getAttackPower());
            }
        }
        for (Unit unit : enemyUnits) {
            if (unit instanceof SiegeUnit && unit.reachedTarget(playerCastle)) {
                playerCastle.takeDamage(unit.getAttackPower());
            }
        }
    }

    public void placeUnit(Unit unit, int x, int y) {
        unit.setPosition(x, y);
        playerUnits.add(unit);
    }

    public void placeBuilding(Building building, int x, int y) {
        building.setPosition(x, y);
        playerBuildings.add(building);
    }

    public int getPlayerResources() {
        return playerResources;
    }

    public List<Unit> getPlayerUnits() {
        return playerUnits;
    }

    public List<Building> getPlayerBuildings() {
        return playerBuildings;
    }
}

// Castle: 城を表現する
class Castle {
    private int health;

    public Castle(int health) {
        this.health = health;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    public int getHealth() {
        return health;
    }
}

// Unit: ユニットの基底クラス
abstract class Unit {
    protected int health;
    protected int attackPower;
    protected int speed;
    protected int level;
    protected int x, y;

    public Unit(int health, int attackPower, int speed) {
        this.health = health;
        this.attackPower = attackPower;
        this.speed = speed;
        this.level = 1;
    }

    public abstract void move();

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public int getAttackPower() {
        return attackPower * level;
    }

    public void levelUp() {
        level++;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean reachedTarget(Castle target) {
        return Math.abs(this.x - target.getHealth()) < 10;
    }
}

// DefensiveUnit: 防衛ユニット
class DefensiveUnit extends Unit {
    public DefensiveUnit() {
        super(100, 10, 2);
    }

    @Override
    public void move() {
        // 防衛ユニットは自陣の中を移動する
    }
}

// SiegeUnit: 攻城ユニット
class SiegeUnit extends Unit {
    public SiegeUnit() {
        super(80, 20, 5);
    }

    @Override
    public void move() {
        x += speed;
    }
}

// Building: 建物の基底クラス
abstract class Building {
    protected int health;
    protected int level;
    protected int x, y;

    public Building(int health) {
        this.health = health;
        this.level = 1;
    }

    public abstract void performAction();

    public void levelUp() {
        level++;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

// ResourceBuilding: 資源設備
class ResourceBuilding extends Building {
    public ResourceBuilding() {
        super(100);
    }

    public int generateResources() {
        return 10 * level;
    }

    @Override
    public void performAction() {
        // 資源生成ロジック
    }
}

// DefenseBuilding: 防衛設備
class DefenseBuilding extends Building {
    public DefenseBuilding() {
        super(150);
    }

    @Override
    public void performAction() {
        // 防衛ロジック
    }
}

// GameView: 画面描画
class GameView extends JPanel {
    private GameModel model;

    public GameView(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(800, 600));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString("Player Resources: " + model.getPlayerResources(), 10, 10);

        for (Unit unit : model.getPlayerUnits()) {
            g.fillRect(unit.x, unit.y, 10, 10);
        }

        for (Building building : model.getPlayerBuildings()) {
            g.fillRect(building.x, building.y, 20, 20);
        }
    }
}

// GameController: ゲーム制御
class GameController {
    private GameModel model;
    private GameView view;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
    }

    public void handleMouseClick(int x, int y) {
        // クリック位置に応じた操作
    }

    public void handleDragAndDrop(int startX, int startY, int endX, int endY) {
        // ドラッグアンドドロップ処理
    }

    public void updateGame() {
        model.updateGameState();
        view.repaint();
    }
}

// メインクラス
public class TowerDefense extends JFrame {

    public TowerDefense() {
        GameModel model = new GameModel();
        GameView view = new GameView(model);
        GameController controller = new GameController(model, view);

        this.setTitle("Tower Defense");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(view);
        this.pack();
        this.setVisible(true);

        // ゲームループ
        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.increaseResources();
                controller.updateGame();
            }
        });
        timer.start();
    } 
    public static void main(String argv[]) {
        new TowerDefense();        
    }
}
