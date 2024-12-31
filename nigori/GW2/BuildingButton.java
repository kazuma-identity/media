import javax.swing.JButton;
import java.awt.event.ActionListener;

public class BuildingButton extends JButton {
  private String action;
  private GamePanel gamePanel;
  private boolean isServerPlayer;

  public BuildingButton(String text, String action, GamePanel panel, boolean isServerPlayer) {
    super(text);
    this.action = action;
    this.gamePanel = panel;
    this.isServerPlayer = isServerPlayer;

    addActionListener(e -> {
      gamePanel.addPlayerInput(this.action, this.isServerPlayer);
    });
  }
}