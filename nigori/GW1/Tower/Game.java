import java.util.List;
import java.util.ArrayList;

public class Game {
  private Player player;
  private Player bot;
  private List<Unit> units;
  private List<Projectile> projectiles;
  private GamePanel gamePanel;

  // 基礎資源生成速度（資源/秒）
  private final double baseResourceRate = 40.0;

  public Game() {
    units = new ArrayList<>();
    projectiles = new ArrayList<>();
  }

  public void setPlayers(Player player, Player bot) {
    this.player = player;
    this.bot = bot;
  }

  public Player getPlayer() {
    return player;
  }

  public Player getBot() {
    return bot;
  }

  public List<Unit> getUnits() {
    return units;
  }

  public List<Projectile> getProjectiles() {
    return projectiles;
  }

  public void addUnit(Unit unit) {
    units.add(unit);
  }

  public void removeUnit(Unit unit) {
    units.remove(unit);
  }

  public void addProjectile(Projectile p) {
    projectiles.add(p);
  }

  public void removeProjectile(Projectile p) {
    projectiles.remove(p);
  }

  public Player getOpponent(Unit unit) {
    if (unit == null)
      return null;
    if (player.getUnits().contains(unit)) {
      return bot;
    } else if (bot.getUnits().contains(unit)) {
      return player;
    }
    return null;
  }

  public List<Player> getPlayers() {
    List<Player> players = new ArrayList<>();
    players.add(player);
    players.add(bot);
    return players;
  }

  public void setGamePanel(GamePanel panel) {
    this.gamePanel = panel;
  }

  public void update(double deltaTime) {
    // 基礎資源生成の更新
    player.addResources((int) (baseResourceRate * deltaTime));
    bot.addResources((int) (baseResourceRate * deltaTime));

    // 資源施設による追加資源生成
    for (Building building : player.getBuildings()) {
      if (building instanceof ResourceBuilding) {
        player.addResources((int) (1.5 * baseResourceRate * deltaTime));
      }
    }

    for (Building building : bot.getBuildings()) {
      if (building instanceof ResourceBuilding) {
        bot.addResources((int) (1.5 * baseResourceRate * deltaTime));
      }
    }

    // 建物の更新
    for (Building building : player.getBuildings()) {
      building.update(deltaTime, this);
    }

    for (Building building : bot.getBuildings()) {
      building.update(deltaTime, this);
    }

    // ユニットの更新
    for (Unit unit : new ArrayList<>(units)) { // コピーリストを使用してConcurrentModificationExceptionを防ぐ
      unit.update(deltaTime, this);
    }

    // プロジェクタイルの更新
    for (Projectile p : new ArrayList<>(projectiles)) {
      p.update(deltaTime, this);
      if (!p.isActive()) {
        removeProjectile(p);
      }
    }

    // 勝敗判定
    if (player.getCastle().getHp() <= 0) {
      if (gamePanel != null) {
        gamePanel.showGameOver(bot.getName());
      }
    }

    if (bot.getCastle().getHp() <= 0) {
      if (gamePanel != null) {
        gamePanel.showGameOver(player.getName());
      }
    }
  }

  // 画面サイズに基づいてユニットが陣地内にいるか判定
  public boolean isWithinTerritory(Player player, double x, double y) {
    if (player.getCastle().getX() < 400) { // 左側プレイヤー
      return x <= 400;
    } else { // 右側プレイヤー
      return x >= 400;
    }
  }
}
