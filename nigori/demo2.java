import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class MyModel {
    private String screenState;

    public MyModel() {
        this.screenState = "mainScreen"; // 初期状態
    }

    public String getScreenState() {
        return screenState;
    }

    public void setScreenState(String screenState) {
        this.screenState = screenState;
    }
}

class MyView extends JFrame {
    private JPanel currentPanel;

    public MyView() {
        this.setTitle("Tower Deffence");
        this.setSize(1000, 1000);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        currentPanel = new JPanel();
        currentPanel.setLayout(new GridLayout(2,1));
        currentPanel.add(new JLabel("Main Screen"));
        currentPanel.add(new JButton("Start"));

        this.setVisible(true);
    }

    public void updateScreen(String screenState) {
        this.remove(currentPanel);

        if ("mainScreen".equals(screenState)) {
            currentPanel = new JPanel();
            currentPanel.setLayout(new GridLayout(2,1));
            currentPanel.add(new JLabel("Tower Deffence"));
            currentPanel.add(new JButton("Start"));
        } else if ("nextScreen".equals(screenState)) {
            currentPanel = new JPanel();
            currentPanel.add(new JLabel("Next Screen"));
        } else if ("finalScreen".equals(screenState)) {
            currentPanel = new JPanel();
            currentPanel.add(new JLabel("Final Screen"));
        }

        this.add(currentPanel, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }
}

class MyController implements ActionListener{
    private MyModel model;
    private MyView view;

    public MyController(MyModel model, MyView view) {
        this.model = model;
        this.view = view;

        // 初期状態のリスナーを設定
        setupMouseListener();
    }

    private void setupMouseListener() {
        // 現在のリスナーをすべて削除して新しいリスナーを登録
        for (var listener : view.getMouseListeners()) {
            view.removeMouseListener(listener);
        }

        view.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick();
            }
        });
    }

    private void handleMouseClick() {
        String currentState = model.getScreenState();

        if ("mainScreen".equals(currentState)) {
            // 次の画面へ移行
            model.setScreenState("nextScreen");
            view.updateScreen(model.getScreenState());
            setupMouseListener(); // リスナーを更新

        } else if ("nextScreen".equals(currentState)) {
            // 最終画面へ移行
            model.setScreenState("finalScreen");
            view.updateScreen(model.getScreenState());
            setupMouseListener(); // リスナーを更新

        } else if ("finalScreen".equals(currentState)) {
            // 初期画面に戻る
            model.setScreenState("mainScreen");
            view.updateScreen(model.getScreenState());
            setupMouseListener(); // リスナーを更新
        }
    }
}

class MVCDemo {
    public static void main(String[] args) {
        MyModel model = new MyModel();
        MyView view = new MyView();
        MyController controller = new MyController(model, view);
    }
}
