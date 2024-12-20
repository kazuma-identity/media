import javax.swing.JPanel;
import java.awt.GridLayout;

public class SelectionPanel extends JPanel {
  private GamePanel gamePanel;

  public SelectionPanel(GamePanel panel) {
    this.gamePanel = panel;
    setLayout(new GridLayout(1, 4));

    // 建物選択ボタン
    BuildingButton buildResourceBtn = new BuildingButton("<html>資源設備<br>必要資源: 100</html>", "BuildResource", gamePanel);
    BuildingButton buildDefenseBtn = new BuildingButton("<html>防衛設備<br>必要資源: 100</html>", "BuildDefense", gamePanel);
    add(buildResourceBtn);
    add(buildDefenseBtn);

    // ユニット選択ボタン
    UnitButton deploySiegeBtn = new UnitButton("<html>攻城ユニット<br>必要資源: 100</html>", "DeploySiege", gamePanel);
    UnitButton deployDefenseBtn = new UnitButton("<html>防衛ユニット<br>必要資源: 100</html>", "DeployDefense", gamePanel);
    add(deploySiegeBtn);
    add(deployDefenseBtn);
  }
}
