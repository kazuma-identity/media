import java.io.*;

public class GameServer {
    public static void main(String[] args) {
        CommServer server1 = new CommServer(5000);
        CommServer server2 = new CommServer(5001);
        
        System.out.println("サーバー準備完了。クライアントを待機中...");
        
        // ゲームの初期化
        Player player1 = new Player("Player1", 100);
        Player player2 = new Player("Player2", 100);
        Game game = new Game();
        game.setPlayers(player1, player2);

        // 非同期でクライアントの処理を開始
        new Thread(() -> handleClient(server1, game, player1)).start();
        new Thread(() -> handleClient(server2, game, player2)).start();
    }

    private static void handleClient(CommServer server, Game game, Player player) {
        try {
            while (true) {
                // クライアントの操作を受信
                String msg = server.recv();
                if (msg != null) {
                    processClientAction(game, player, msg);

                    // 全クライアントにゲーム状態を送信
                    String gameState = serializeGameState(game);
                    server.send(gameState);
                }
            }
        } catch (Exception e) {
            System.err.println("クライアントとの通信でエラーが発生しました: " + e.getMessage());
        }
    }

    private static void processClientAction(Game game, Player player, String action) {
        // クライアントからの指示に基づいてゲーム状態を更新
        // メッセージ例: "Build:RESOURCE:200:300"
        String[] parts = action.split(":");
        if (parts.length == 4) {
            String command = parts[0];
            String type = parts[1];
            double x = Double.parseDouble(parts[2]);
            double y = Double.parseDouble(parts[3]);

            if (command.equals("Build")) {
                if (type.equals("RESOURCE")) {
                    player.addBuilding(new ResourceBuilding(x, y, player));
                } else if (type.equals("DEFENSE")) {
                    player.addBuilding(new DefenseBuilding(x, y, player));
                }
            } else if (command.equals("Deploy")) {
                if (type.equals("SIEGE")) {
                    game.addUnit(new SiegeUnit(x, y, 1.0, 100, player));
                } else if (type.equals("DEFENSE")) {
                    game.addUnit(new DefenseUnit(x, y, player));
                }
            }
        }
    }

    private static String serializeGameState(Game game) {
        // ゲーム状態を文字列に変換
        // 必要に応じて詳細に実装
        StringBuilder sb = new StringBuilder();
        for (Player p : game.getPlayers()) {
            sb.append(p.getName()).append(":").append(p.getResources()).append(";");
        }
        return sb.toString();
    }
}
