import javax.swing.JPanel;
import java.awt.GridLayout;

public class SelectionPanel extends JPanel {
    private GamePanel gamePanel;
    private boolean isServerPlayer;

    public SelectionPanel(GamePanel panel, boolean isServerPlayer) {
        this.gamePanel = panel;
        this.isServerPlayer = isServerPlayer;
        setLayout(new GridLayout(1, 4));

        // 建物選択ボタン
        BuildingButton buildResourceBtn = new BuildingButton("資源設備", "BuildResource", gamePanel, isServerPlayer);
        BuildingButton buildDefenseBtn = new BuildingButton("防衛設備", "BuildDefense", gamePanel, isServerPlayer);
        add(buildResourceBtn);
        add(buildDefenseBtn);

        // ユニット選択ボタン
        UnitButton deploySiegeBtn = new UnitButton("攻城ユニット", "DeploySiege", gamePanel, isServerPlayer);
        UnitButton deployDefenseBtn = new UnitButton("防衛ユニット", "DeployDefense", gamePanel, isServerPlayer);
        add(deploySiegeBtn);
        add(deployDefenseBtn);
    }
}
