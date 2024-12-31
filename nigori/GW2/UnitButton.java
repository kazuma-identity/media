import javax.swing.JButton;
import java.awt.event.ActionListener;

public class UnitButton extends JButton {
  private String action;
  private GamePanel gamePanel;
  private boolean isServerPlayer;

  public UnitButton(String text, String action, GamePanel panel, boolean isServerPlayer) {
    super(text);
    this.action = action;
    this.gamePanel = panel;
    this.isServerPlayer = isServerPlayer;

    addActionListener(e -> {
      gamePanel.addPlayerInput(this.action, this.isServerPlayer);
    });
  }
}
