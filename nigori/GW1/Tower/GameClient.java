import javax.swing.*;
import java.io.*;

public class GameClient {
    private static Game game;
    private static GamePanel gamePanel;

    public static void main(String[] args) {
        CommClient client = new CommClient("localhost", 5000);
        System.out.println("サーバーに接続しました。");

        // サーバーからの状態を受信するスレッドを開始
        new Thread(() -> {
            while (true) {
                String gameState = client.recv();
                if (gameState != null) {
                    updateGameUI(gameState);
                }
            }
        }).start();

        // クライアントの操作をサーバーに送信する例
        client.send("Build:RESOURCE:200:300");
    }

    private static void updateGameUI(String gameState) {
        // サーバーから受信したゲーム状態をGameオブジェクトに反映
        SwingUtilities.invokeLater(() -> {
            game.updateFromString(gameState); // Gameオブジェクトを更新
            gamePanel.repaint(); // GamePanelを再描画
        });
    }
}
