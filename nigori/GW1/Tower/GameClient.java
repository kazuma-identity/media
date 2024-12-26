import javax.swing.*;

public class GameClient {
    public static void main(String[] args) {
        // サーバーに接続
        CommClient client = new CommClient("localhost", 5000);
        System.out.println("サーバーに接続しました。");

        // ゲーム画面の初期化
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("タワーディフェンスゲーム");
            Game game = new Game(); // Gameオブジェクトの初期化
            GamePanel gamePanel = new GamePanel(client); // CommClientを渡す
            gamePanel.setGame(game);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(gamePanel);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        // サーバーからの状態を受信してゲームを更新
        new Thread(() -> {
            while (true) {
                String gameState = client.recv();
                if (gameState != null) {
                    updateGameState(gameState);
                }
            }
        }).start();
    }

    private static void updateGameState(String gameState) {
        // サーバーから受信したゲーム状態をGameオブジェクトに反映
        SwingUtilities.invokeLater(() -> {
            // Gameオブジェクトを更新
            Game updatedGame = Game.fromString(gameState);
            // GamePanelの再描画（ゲーム状態更新はGamePanelで実装）
        });
    }
}
