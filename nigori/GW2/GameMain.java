import javax.swing.*;
import java.io.*;

public class GameMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String[] options = {"サーバー", "クライアント"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "ゲームモードを選択してください:",
                    "マルチプレイヤーモード",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 0) {
                startServerMode();
            } else if (choice == 1) {
                startClientMode();
            } else {
                System.exit(0);
            }
        });
    }

    private static void startServerMode() {
        try {
            int port = Integer.parseInt(JOptionPane.showInputDialog("サーバーポート番号を入力してください (例: 12345):"));
            CommServer server = new CommServer(port);
            JOptionPane.showMessageDialog(null, "サーバーがポート " + port + " で待機しています。");

            // サーバープレイヤーの設定
            String serverPlayerName = JOptionPane.showInputDialog("サーバー側プレイヤー名を入力してください:");
            if (serverPlayerName == null || serverPlayerName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "プレイヤー名が無効です。ゲームを終了します。");
                System.exit(0);
            }

            Player serverPlayer = new Player(serverPlayerName, 100);
            Castle serverCastle = new Castle(100, 300, 1000);
            serverPlayer.setCastle(serverCastle);

            Player clientPlayer = new Player("Opponent", 100);
            Castle clientCastle = new Castle(700, 300, 1000);
            clientPlayer.setCastle(clientCastle);

            Game game = new Game();
            game.setPlayers(serverPlayer, clientPlayer);

            GamePanel gamePanel = new GamePanel();
            gamePanel.setGame(game);
            game.setGamePanel(gamePanel);

            JFrame frame = new JFrame("サーバー: タワーディフェンスゲーム");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(gamePanel);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // ゲームループ
            new Thread(() -> {
                while (true) {
                    String clientInput = server.recv();
                    if (clientInput != null) {
                        game.updateFromServerState(clientInput);
                    }

                    // サーバー側プレイヤーの操作を反映
                    game.updateServerPlayerInput(gamePanel.getServerPlayerInput());

                    // ゲーム状態をクライアントに送信
                    server.send(game.getStateAsString());
                    try {
                        Thread.sleep(16); // 60FPS
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "エラー: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void startClientMode() {
        try {
            String host = JOptionPane.showInputDialog("接続先サーバーのホスト名を入力してください:");
            int port = Integer.parseInt(JOptionPane.showInputDialog("接続先サーバーのポート番号を入力してください (例: 12345):"));

            CommClient client = new CommClient(host, port);
            JOptionPane.showMessageDialog(null, "サーバーに接続しました。");

            // クライアントプレイヤーの設定
            String clientPlayerName = JOptionPane.showInputDialog("クライアント側プレイヤー名を入力してください:");
            if (clientPlayerName == null || clientPlayerName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "プレイヤー名が無効です。ゲームを終了します。");
                System.exit(0);
            }

            Player clientPlayer = new Player(clientPlayerName, 100);
            Castle clientCastle = new Castle(700, 300, 1000);
            clientPlayer.setCastle(clientCastle);

            Player serverPlayer = new Player("Opponent", 100);
            Castle serverCastle = new Castle(100, 300, 1000);
            serverPlayer.setCastle(serverCastle);

            Game game = new Game();
            game.setPlayers(serverPlayer, clientPlayer);

            GamePanel gamePanel = new GamePanel();
            gamePanel.setGame(game);
            game.setGamePanel(gamePanel);

            JFrame frame = new JFrame("クライアント: タワーディフェンスゲーム");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(gamePanel);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // ゲームループ
            new Thread(() -> {
                while (true) {
                    // クライアント側プレイヤーの操作を送信
                    client.send(gamePanel.getClientPlayerInput());

                    String serverState = client.recv();
                    if (serverState != null) {
                        game.updateFromServerState(serverState);
                    }
                    try {
                        Thread.sleep(16); // 60FPS
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "エラー: " + e.getMessage());
            System.exit(1);
        }
    }
}
