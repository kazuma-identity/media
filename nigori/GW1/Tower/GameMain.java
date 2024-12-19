import javax.swing.*;
import java.awt.*;

public class GameMain {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      // プレイヤー名入力用のカスタムダイアログ
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
      GamePanel gamePanel = new GamePanel();
      gamePanel.setGame(game);
      game.setGamePanel(gamePanel);

      // ボットの作成と開始
      Bot botAI = new Bot(game, bot, player);
      botAI.start();

      // JFrameの設定
      JFrame frame = new JFrame("対戦型タワーディフェンスゲーム");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.add(gamePanel);
      frame.setSize(800, 600);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    });
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
    textField.setPreferredSize(new Dimension(200, textField.getFontMetrics(textField.getFont()).getHeight() + 10));
    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // コンパクトに配置
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

