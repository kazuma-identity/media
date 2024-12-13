import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;

public class Bot {
  private Player botPlayer;
  private Player opponent;
  private Game game;
  private Timer botTimer;
  private Random random;

  public Bot(Game game, Player botPlayer, Player opponent) {
    this.game = game;
    this.botPlayer = botPlayer;
    this.opponent = opponent;
    this.random = new Random();
  }

  public void start() {
    botTimer = new Timer();
    botTimer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        performActions();
      }
    }, 0, 5000); // 5秒ごとに行動
  }

  private void performActions() {
    // 攻城ユニットの生成
    if (botPlayer.getResources() >= 100) { // 攻城ユニットのコスト
      double x = botPlayer.getCastle().getX();
      double y = botPlayer.getCastle().getY() + random.nextInt(100) - 50; // 城周辺に配置
      SiegeUnit siegeUnit = new SiegeUnit(x, y, 1.0, 100, botPlayer);
      botPlayer.addUnit(siegeUnit);
      game.addUnit(siegeUnit);
      botPlayer.spendResources(100);
    }

    // 防衛ユニットの生成
    if (botPlayer.getResources() >= 50) { // 防衛ユニットのコスト
      double x = botPlayer.getCastle().getX() + random.nextInt(100) - 50;
      double y = botPlayer.getCastle().getY() + random.nextInt(100) - 50;
      DefenseUnit defenseUnit = new DefenseUnit(x, y, botPlayer);
      botPlayer.addUnit(defenseUnit);
      game.addUnit(defenseUnit);
      botPlayer.spendResources(50);
    }

    // 資源生成建物のアップグレード
    for (Building building : botPlayer.getBuildings()) {
      if (building instanceof ResourceBuilding) {
        int upgradeCost = building.getCost() * building.getLevel();
        if (botPlayer.spendResources(upgradeCost)) {
          building.upgrade();
        }
      }
    }
  }

  public void stop() {
    botTimer.cancel();
  }
}
