import java.util.List;
import java.util.ArrayList;

public class Game {
  private Player serverPlayer;
  private Player clientPlayer;
  private List<Unit> units;
  private List<Projectile> projectiles;
  private GamePanel gamePanel;

  // 基礎資源生成速度（資源/秒）
  private final double baseResourceRate = 40.0;

  public Game() {
    units = new ArrayList<>();
    projectiles = new ArrayList<>();
  }

  public void setPlayers(Player serverPlayer, Player clientPlayer) {
    this.serverPlayer = serverPlayer;
    this.clientPlayer = clientPlayer;
  }

  public Player getServerPlayer() {
    return serverPlayer;
  }

  public Player getClientPlayer() {
    return clientPlayer;
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

  public List<Player> getPlayers() {
    List<Player> players = new ArrayList<>();
    players.add(serverPlayer);
    players.add(clientPlayer);
    return players;
  }

  public void setGamePanel(GamePanel panel) {
    this.gamePanel = panel;
  }

  public void update(double deltaTime) {
    // 基礎資源生成の更新
    serverPlayer.addResources((int) (baseResourceRate * deltaTime));
    clientPlayer.addResources((int) (baseResourceRate * deltaTime));

    // 資源施設による追加資源生成
    for (Building building : serverPlayer.getBuildings()) {
      if (building instanceof ResourceBuilding) {
        serverPlayer.addResources((int) (1.5 * baseResourceRate * deltaTime));
      }
    }

    for (Building building : clientPlayer.getBuildings()) {
      if (building instanceof ResourceBuilding) {
        clientPlayer.addResources((int) (1.5 * baseResourceRate * deltaTime));
      }
    }

    // 建物の更新
    for (Building building : serverPlayer.getBuildings()) {
      building.update(deltaTime, this);
    }

    for (Building building : clientPlayer.getBuildings()) {
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
    if (serverPlayer.getCastle().getHp() <= 0) {
      if (gamePanel != null) {
        gamePanel.showGameOver(clientPlayer.getName());
      }
    }

    if (clientPlayer.getCastle().getHp() <= 0) {
      if (gamePanel != null) {
        gamePanel.showGameOver(serverPlayer.getName());
      }
    }
  }

  public void updateServerPlayerInput(String input) {
    if (input == null) return;

    switch (input) {
      case "BuildResource":
        serverPlayer.buildResource();
        break;
      case "BuildDefense":
        serverPlayer.buildDefense();
        break;
      case "DeploySiege":
        serverPlayer.deploySiege();
        break;
      case "DeployDefense":
        serverPlayer.deployDefense();
        break;
      default:
        System.out.println("サーバープレイヤーの不明な入力: " + input);
    }
  }

  public void updateClientPlayerInput(String input) {
    if (input == null) return;

    switch (input) {
      case "BuildResource":
        clientPlayer.buildResource();
        break;
      case "BuildDefense":
        clientPlayer.buildDefense();
        break;
      case "DeploySiege":
        clientPlayer.deploySiege();
        break;
      case "DeployDefense":
        clientPlayer.deployDefense();
        break;
      default:
        System.out.println("クライアントプレイヤーの不明な入力: " + input);
    }
  }

  public String getStateAsString() {
    StringBuilder state = new StringBuilder();

    // プレイヤー情報
    for (Player player : getPlayers()) {
      state.append(player.getName()).append(",")
          .append(player.getResources()).append(",")
          .append(player.getCastle().getHp()).append(";");

      // 建物情報
      for (Building building : player.getBuildings()) {
        state.append("B,") // BはBuildingの識別子
            .append(building.getX()).append(",")
            .append(building.getY()).append(";");
      }

      // ユニット情報
      for (Unit unit : player.getUnits()) {
        state.append("U,") // UはUnitの識別子
            .append(unit.getType()).append(",")
            .append(unit.getX()).append(",")
            .append(unit.getY()).append(",")
            .append(unit.getHp()).append(";");
      }
    }

    // プロジェクタイル情報
    for (Projectile p : getProjectiles()) {
      state.append("P,").append(p.serialize()).append(";");
    }

    return state.toString();
  }

  public void updateFromServerState(String gameState) {
    String[] entries = gameState.split(";");

    for (String entry : entries) {
      if (entry.isEmpty()) continue;

      String[] fields = entry.split(",");

      if (fields.length < 3) continue;

      if (fields[0].equals("B")) { // 建物情報
        double x = Double.parseDouble(fields[1]);
        double y = Double.parseDouble(fields[2]);
        Building building = new ResourceBuilding(x, y, null); // プレイヤーを後で設定
        Player player = determinePlayerForBuilding(x);
        if (player != null) {
          player.addBuilding(building);
        }
      } else if (fields[0].equals("U")) { // ユニット情報
        UnitType type = UnitType.valueOf(fields[1]);
        double x = Double.parseDouble(fields[2]);
        double y = Double.parseDouble(fields[3]);
        double hp = Double.parseDouble(fields[4]);
        Unit unit = new DefenseUnit(x, y, null);
        Player player = determinePlayerForUnit(x);
        if (player != null) {
          player.addUnit(unit);
        }
      } else if (fields[0].equals("P")) { // プロジェクタイル情報
        String projectileData = entry.substring(2); // "P,"を除去
        Projectile projectile = new Projectile(projectileData);
        addProjectile(projectile);
      }
    }
  }

  private Player determinePlayerForBuilding(double x) {
    for (Player p : getPlayers()) {
      if (isWithinTerritory(p, x, 0)) {
        return p;
      }
    }
    return null;
  }

  private Player determinePlayerForUnit(double x) {
    return determinePlayerForBuilding(x);
  }

  public boolean isWithinTerritory(Player player, double x, double y) {
    if (player.getCastle().getX() < 400) { // 左側プレイヤー
      return x <= 400;
    } else { // 右側プレイヤー
      return x >= 400;
    }
  }

  public Player getOpponent(Unit unit) {
    if (unit.getOwner() == serverPlayer) {
        return clientPlayer;
    } else if (unit.getOwner() == clientPlayer) {
        return serverPlayer;
    }
    return null;
  }

}
