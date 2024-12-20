import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends JPanel {
  private Game game;
  private String selectedAction = null; // "BuildResource", "BuildDefense", "DeploySiege", "DeployDefense"
  private Timer gameTimer;

  public GamePanel() {
    setBackground(Color.BLACK);
    setLayout(new BorderLayout());

    // SelectionPanelの追加
    SelectionPanel selectionPanel = new SelectionPanel(this);
    add(selectionPanel, BorderLayout.SOUTH);

    // マウスリスナーの追加
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (selectedAction == null)
          return;

        double x = (e.getX() / 30) * 30 + 15;
        double y = (e.getY() / 30) * 30 + 15;

        Player currentPlayer = game.getPlayer();
        if (currentPlayer == null)
          return;

        if (!isWithinTerritory(currentPlayer, x, y)) {
          JOptionPane.showMessageDialog(null, "自分の陣地内にのみ建物やユニットを配置できます。");
          return;
        }

        if (selectedAction.equals("BuildResource")) {
          buildBuilding(BuildingType.RESOURCE, x, y);
        } else if (selectedAction.equals("BuildDefense")) {
          buildBuilding(BuildingType.DEFENSE, x, y);
        } else if (selectedAction.equals("DeploySiege")) {
          deployUnit(UnitType.SIEGE, x, y);
        } else if (selectedAction.equals("DeployDefense")) {
          deployUnit(UnitType.DEFENSE, x, y);
        }
      }
    });

    // ゲームループの設定（60FPS）
    gameTimer = new Timer(16, new ActionListener() { // 約60FPS
      private long lastTime = System.nanoTime();

      @Override
      public void actionPerformed(ActionEvent e) {
        long currentTime = System.nanoTime();
        double deltaTime = (currentTime - lastTime) / 1_000_000_000.0; // 秒単位
        lastTime = currentTime;
        game.update(deltaTime);
        repaint();
      }
    });
    gameTimer.start();
  }

  public void setGame(Game game) {
    this.game = game;
  }

  private boolean isWithinTerritory(Player player, double x, double y) {
    // 自分の陣地内にのみ建物やユニットを配置できるように判定
    // マップは横長で左右に陣地がある
    if (player.getCastle().getX() < 400) { // 左側プレイヤー
      return x <= 400;
    } else { // 右側プレイヤー
      return x >= 400;
    }
  }

  private void buildBuilding(BuildingType type, double x, double y) {
    Player player = game.getPlayer();
    if (player == null)
      return;

    int cost = (type == BuildingType.RESOURCE) ? 100 : 150;
    if (player.getResources() >= cost) {
      player.spendResources(cost);
      Building building;
      if (type == BuildingType.RESOURCE) {
        building = new ResourceBuilding(x, y, player);
      } else {
        building = new DefenseBuilding(x, y, player);
      }
      player.addBuilding(building);
      repaint();
    } else {
      JOptionPane.showMessageDialog(null, "資源が不足しています。");
    }
  }

  private void deployUnit(UnitType type, double x, double y) {
    Player player = game.getPlayer();
    if (player == null)
      return;

    int cost = (type == UnitType.DEFENSE) ? 50 : 100;
    if (player.getResources() >= cost) {
      player.spendResources(cost);
      Unit unit;
      if (type == UnitType.DEFENSE) {
        unit = new DefenseUnit(x, y, player);
      } else {
        unit = new SiegeUnit(x, y, 1.0, cost, player);
      }
      player.addUnit(unit);
      game.addUnit(unit);
      repaint();
    } else {
      JOptionPane.showMessageDialog(null, "資源が不足しています。");
    }
  }

  public void setSelectedAction(String action) {
    this.selectedAction = action;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (game != null) {
      // 資源のプログレスバーの表示
      Player player = game.getPlayer();
      Player bot = game.getBot();

      // プレイヤーの資源表示
      g.setColor(Color.WHITE);
      g.drawString(player.getName() + " 資源: " + player.getResources(), 10, 20);
      g.setColor(Color.GREEN);
      int playerMaxResources = 200; // 資源の最大値（スケーリング用）
      int playerResourceWidth = Math.min(player.getResources(), playerMaxResources);
      g.fillRect(100, 10, playerResourceWidth, 10);
      g.setColor(Color.GRAY);
      g.drawRect(100, 10, playerMaxResources, 10);

      // ボットの資源表示
      g.setColor(Color.WHITE);
      g.drawString(bot.getName() + " 資源: " + bot.getResources(), 650, 20);
      g.setColor(Color.GREEN);
      int botMaxResources = 200; // 資源の最大値（スケーリング用）
      int botResourceWidth = Math.min(bot.getResources(), botMaxResources);
      g.fillRect(650, 10, botResourceWidth, 10);
      g.setColor(Color.GRAY);
      g.drawRect(650, 10, botMaxResources, 10);

      // 城の描画
      for (Player p : game.getPlayers()) {
        if (p.getName().equals("Bot")) {
          p.getCastle().draw(g, Color.RED);
        } else {
          p.getCastle().draw(g, Color.BLUE);
        }
      }

      // 建物の描画
      for (Player p : game.getPlayers()) {
        for (Building building : p.getBuildings()) {
          building.draw(g);
          // 各建物のレベルを表示
          g.setColor(Color.WHITE);
          g.drawString("Lv " + building.getLevel(), (int) building.getX() - 10, (int) building.getY() + 25);
        }
      }

      // ユニットの描画
      for (Player p : game.getPlayers()) {
        for (Unit unit : p.getUnits()) {
          unit.draw(g);
        }
      }

      // プロジェクタイルの描画
      for (Projectile p : game.getProjectiles()) {
        p.draw(g);
      }
    }
  }

  public void showGameOver(String winner) {
    JOptionPane.showMessageDialog(this, "ゲーム終了！勝者: " + winner);
    System.exit(0);
  }
}
