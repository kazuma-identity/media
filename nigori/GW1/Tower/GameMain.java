import javax.swing.*;
import java.awt.*;

public class GameMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // モード選択ダイアログを表示
            String[] options = {"ボット対戦", "クライアント対戦"};
            int mode = JOptionPane.showOptionDialog(
                    null,
                    "モードを選択してください。",
                    "ゲームモード選択",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (mode == 0) {
                // ボット対戦モード
                startBotMode();
            } else if (mode == 1) {
                // クライアント対戦モード
                startClientMode();
            } else {
                // キャンセルまたはクローズ
                JOptionPane.showMessageDialog(null, "ゲームを終了します。");
                System.exit(0);
            }
        });
    }

    private static void startBotMode() {
        // プレイヤー名入力
        String playerName = showCustomInputDialog();
        if (playerName == null || playerName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "プレイヤー名が無効です。ゲームを終了します。");
            System.exit(0);
        }

        // プレイヤーとボットの作成
        Player player = new Player(playerName, 100);
        Castle playerCastle = new Castle(100, 300, 1000);
        player.setCastle(playerCastle);

        Player bot = new Player("Bot", 100);
        Castle botCastle = new Castle(700, 300, 1000);
        bot.setCastle(botCastle);

        // ゲームの初期化
        Game game = new Game();
        game.setPlayers(player, bot);

        // ゲームパネルの作成
        GamePanel gamePanel = new GamePanel(null); // ボット対戦なのでCommClientは不要
        gamePanel.setGame(game);
        game.setGamePanel(gamePanel);

        // ボットの動作を追加
        Bot botAI = new Bot(game, bot, player);
        botAI.start();

        // JFrameの設定
        JFrame frame = new JFrame("ボット対戦モード");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // ゲーム更新タイマーの開始
        new Timer(16, e -> {
            game.update(0.016); // 約60FPSでゲーム状態を更新
            gamePanel.repaint();
        }).start();
    }


    private static void startClientMode() {
        // プレイヤー名入力
        String playerName = showCustomInputDialog();
        if (playerName == null || playerName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "プレイヤー名が無効です。ゲームを終了します。");
            System.exit(0);
        }

        // サーバーに接続
        CommClient client = new CommClient("localhost", 5000);
        System.out.println("サーバーに接続しました。");

        // ゲームパネルの作成
        GamePanel gamePanel = new GamePanel(client);
        Game game = new Game();
        gamePanel.setGame(game);

        // サーバーからの状態を受信してゲームを更新
        new Thread(() -> {
            while (true) {
                String gameState = client.recv();
                if (gameState != null) {
                    SwingUtilities.invokeLater(() -> gamePanel.updateFromServer(gameState));
                }
            }
        }).start();

        // JFrameの設定
        JFrame frame = new JFrame("クライアント対戦モード");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static String showCustomInputDialog() {
        // ダイアログの作成
        JDialog dialog = new JDialog((Frame) null, "タワーディフェンスゲーム", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(null);

        // メッセージと入力フィールド
        JLabel messageLabel = new JLabel("プレイヤー名を入力してください:");
        JTextField textField = new JTextField(20);
        JPanel inputPanel = new JPanel();
        inputPanel.add(messageLabel);
        inputPanel.add(textField);

        // ボタンの作成
        JButton okButton = new JButton("ゲーム開始");
        JButton cancelButton = new JButton("ゲーム終了");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // ダイアログに追加
        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // ボタンの動作
        final String[] result = {null};
        okButton.addActionListener(e -> {
            result[0] = textField.getText().trim();
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> {
            result[0] = null;
            dialog.dispose();
        });

        // ダイアログの表示
        dialog.setVisible(true);

        return result[0];
    }
}
