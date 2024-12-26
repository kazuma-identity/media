import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

public class GamePanel extends JPanel {
    private Game game;
    private String selectedAction = null; // "BuildResource", "BuildDefense", "DeploySiege", "DeployDefense"
    private Timer gameTimer;
    private CommClient client; // サーバーとの通信を管理

    // 一時的なメッセージ用のフィールド
    private String temporaryMessage = null;
    private int temporaryMessageDuration = 0; // 表示時間をカウントダウンする変数
    private Timer messageTimer; // 一時的なメッセージのカウントダウン用タイマー

    public GamePanel(CommClient client) {
        this.client = client;

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
        gameTimer = new Timer(16, e -> {
            game.update(0.016); // 約60FPS（1フレームあたりの時間は約0.016秒）
            repaint();
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

            // サーバーに設置情報を送信
            client.send(String.format("Build:%s:%.2f:%.2f:%d",
                    type.name(), x, y, building.getLevel()));
        } else {
            showTemporaryMessage("資源が不足しています。", 20);
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

            // サーバーに配置情報を送信
            client.send(String.format("Deploy:%s:%.2f:%.2f:%d",
                    type.name(), x, y, unit.getLevel()));
        } else {
            showTemporaryMessage("資源が不足しています。", 20);
        }
    }

    public void setSelectedAction(String action) {
        this.selectedAction = action;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (game != null) {
            // 画面描画（省略済み、元コード参照）
        }
    }

    public void showGameOver(String winner) {
        JOptionPane.showMessageDialog(this, "ゲーム終了！勝者: " + winner);
        System.exit(0);
    }

    public void updateFromServer(String gameState) {
        // サーバーから受信したゲーム状態を更新
        Game updatedGame = Game.fromString(gameState);
        this.setGame(updatedGame);
        repaint();
    }
}
