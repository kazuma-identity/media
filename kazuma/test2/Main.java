import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        GameField field = new GameField(10, 10); // 10x10のフィールド
        Castle castle = new Castle(5, 5, 100); // 中央に城を配置
        field.placeCastle(castle);

        Scanner scanner = new Scanner(System.in);
        System.out.println("ゲームスタート！");

        while (!castle.isDestroyed()) {
            System.out.println("\n現在のフィールド:");
            field.display();

            System.out.println("ユニットを配置してください (x y):");
            int x = scanner.nextInt();
            int y = scanner.nextInt();

            Unit unit = new Unit(x, y, 10); // ユニットの攻撃力は10
            if (field.placeUnit(unit)) {
                System.out.println("ユニットを配置しました！");
                unit.attack(castle);
            } else {
                System.out.println("その場所には配置できません。");
            }

            if (castle.isDestroyed()) {
                System.out.println("城が破壊されました！ プレイヤー2の勝利！");
                break;
            }
        }
        scanner.close();
    }
}
