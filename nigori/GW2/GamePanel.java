import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.BorderLayout;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GamePanel extends JPanel {
  private Game game;
  private ConcurrentLinkedQueue<String> serverPlayerInputQueue = new ConcurrentLinkedQueue<>();
  private ConcurrentLinkedQueue<String> clientPlayerInputQueue = new ConcurrentLinkedQueue<>();
  private Timer gameTimer;

  public GamePanel() {
    setBackground(Color.BLACK);
    setLayout(new BorderLayout());

    // サーバー側プレイヤー用のSelectionPanel
    SelectionPanel serverSelectionPanel = new SelectionPanel(this, true); // true: サーバープレイヤー
    add(serverSelectionPanel, BorderLayout.WEST);

    // クライアント側プレイヤー用のSelectionPanel
    SelectionPanel clientSelectionPanel = new SelectionPanel(this, false); // false: クライアントプレイヤー
    add(clientSelectionPanel, BorderLayout.EAST);

    // ゲームループの設定（60FPS）
    gameTimer = new Timer(16, e -> {
      double deltaTime = 16 / 1000.0; // 約60FPS
      game.update(deltaTime);
      repaint();
    });
    gameTimer.start();
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public String getServerPlayerInput() {
    return serverPlayerInputQueue.poll();
  }

  public String getClientPlayerInput() {
    return clientPlayerInputQueue.poll();
  }

  public void addPlayerInput(String input, boolean isServerPlayer) {
    if (isServerPlayer) {
      serverPlayerInputQueue.add(input);
    } else {
      clientPlayerInputQueue.add(input);
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (game != null) {
      // 資源のプログレスバーの表示
      Player serverPlayer = game.getServerPlayer();
      Player clientPlayer = game.getClientPlayer();

      // サーバープレイヤーの資源表示
      g.setColor(Color.WHITE);
      g.drawString(serverPlayer.getName() + " 資源: " + serverPlayer.getResources(), 10, 20);
      g.setColor(Color.GREEN);
      int serverPlayerMaxResources = 200; // 資源の最大値（スケーリング用）
      int serverResourceWidth = Math.min(serverPlayer.getResources(), serverPlayerMaxResources);
      g.fillRect(100, 10, serverResourceWidth, 10);
      g.setColor(Color.GRAY);
      g.drawRect(100, 10, serverPlayerMaxResources, 10);

      // クライアントプレイヤーの資源表示
      g.setColor(Color.WHITE);
      g.drawString(clientPlayer.getName() + " 資源: " + clientPlayer.getResources(), 650, 20);
      g.setColor(Color.GREEN);
      int clientPlayerMaxResources = 200; // 資源の最大値（スケーリング用）
      int clientResourceWidth = Math.min(clientPlayer.getResources(), clientPlayerMaxResources);
      g.fillRect(650, 10, clientResourceWidth, 10);
      g.setColor(Color.GRAY);
      g.drawRect(650, 10, clientPlayerMaxResources, 10);

      // 城の描画
      for (Player p : game.getPlayers()) {
        if (p == serverPlayer) {
          p.getCastle().draw(g, Color.BLUE);
        } else {
          p.getCastle().draw(g, Color.RED);
        }
      }

      // 建物の描画
      for (Player p : game.getPlayers()) {
        for (Building building : p.getBuildings()) {
          building.draw(g);
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
