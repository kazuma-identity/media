import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends JPanel {
    private Game game;
    private String selectedAction = null; // "BuildResource", "BuildDefense", "DeploySiege", "DeployDefense"
    private Timer gameTimer;

    // 一時的なメッセージ用のフィールド
    private String temporaryMessage = null;
    private int temporaryMessageDuration = 0; // 表示時間をカウントダウンする変数
    private Timer messageTimer; // 一時的なメッセージのカウントダウン用タイマー

    public GamePanel() {
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        // SelectionPanelの追加
        SelectionPanel selectionPanel = new SelectionPanel(this);
        add(selectionPanel, BorderLayout.SOUTH);

        // メッセージを非表示にするためのタイマー
        messageTimer = new Timer(100, e -> {
            if (temporaryMessageDuration > 0) {
                temporaryMessageDuration--;
                repaint();
            } else {
                temporaryMessage = null;
                messageTimer.stop();
            }
        });

        // マウスリスナーの追加
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedAction == null)
                    return;

                double x = e.getX();
                double y = e.getY();

                Player currentPlayer = game.getPlayer();
                if (currentPlayer == null)
                    return;

                if (!isWithinTerritory(currentPlayer, x, y)) {
                    showTemporaryMessage("自分の陣地内にのみ建物やユニットを配置できます。", 30);
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

    // 一時的なメッセージを設定するメソッド
    public void showTemporaryMessage(String message, int duration) {
        this.temporaryMessage = message;
        this.temporaryMessageDuration = duration;
        messageTimer.start();
        repaint();
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
            showTemporaryMessage("資源が不足しています。", 20); // 表示メッセージの追加
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
            showTemporaryMessage("資源が不足しています。", 20); // 表示メッセージの追加
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

            // 一時的なメッセージの描画
            if (temporaryMessage != null && temporaryMessageDuration > 0) {
                g.setColor(Color.RED);
                g.setFont(g.getFont().deriveFont(18f)); // フォントサイズを調整

                // メッセージを中央に配置
                int messageWidth = g.getFontMetrics().stringWidth(temporaryMessage);
                int messageX = (getWidth() - messageWidth) / 2; // 画面中央のX座標
                int messageY = getHeight() - 50; // 画面下部から少し上のY座標

                g.drawString(temporaryMessage, messageX, messageY);
            }
        }
    }

    public void showGameOver(String winner) {
        JOptionPane.showMessageDialog(this, "ゲーム終了！勝者: " + winner);
        System.exit(0);
    }

    public void updateFromServer(String gameState) {
        Game updatedGame = Game.fromString(gameState);
        this.setGame(updatedGame);
        repaint();
    }

}
