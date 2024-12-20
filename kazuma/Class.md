随時追加していく

# 各クラスの説明
## 1. GameMain​
プログラムのエントリーポイントです。
ゲームの基本設定を行い、プレイヤー名を入力させます。
プレイヤーとボットを作成し、ゲームの初期状態を設定します。
ゲーム画面を作成し、Swingを使ってGUIを表示します。

## 2. Game​
ゲーム全体の管理を行うクラス。
プレイヤー、ボット、ユニット、建物、プロジェクタイルを管理します。
ゲームループで各要素を更新し、勝敗判定を行います。
資源生成や建物のアップグレードの管理も実施します。
## 3. GamePanel
ゲーム画面を描画するSwingのJPanelクラス。
マウス入力を監視し、建物やユニットをプレイヤーが配置できるようにします。
画面全体の描画処理（ユニット、建物、資源バーなど）を担当します。
## 4. ​SelectionPanel
ユーザーが建物やユニットを選択できるボタンを提供します。
ボタンをクリックすると、選択されたアクションがGamePanelに伝えられます。
## 5. ​Building
建物を表す抽象クラス。
x, y座標、コスト、所有者、レベルなどを持ちます。
サブクラス：
ResourceBuilding: 資源生成用の建物​ResourceBuilding。
DefenseBuilding: 攻撃可能な防衛用の建物​DefenseBuilding。
## 6. Unit
ユニットを表す抽象クラス。
x, y座標、HP、コスト、所有者、移動速度、ユニットタイプ（攻城/防衛）を持ちます。
サブクラス：
SiegeUnit: 攻撃力が高く、城を攻撃するユニット​SiegeUnit。
DefenseUnit: 防衛用のユニット​DefenseUnit。
## 7. Player
プレイヤーの情報を管理します。
所有する建物、ユニット、城、および資源の状態を追跡します。
## 8. Castle​
プレイヤーの城を表します。
HPが0になるとゲーム終了。
## 9. Projectile​
ユニットや建物から発射される弾（プロジェクタイル）を表します。
攻城ユニットに対してダメージを与える仕組みです。
## 10. Bot​
ボットプレイヤーの行動ロジックを管理します。
定期的に資源を使い、ユニットや建物を生成します。
## 11. BuildingType​
建物のタイプ（RESOURCE, DEFENSE）を列挙型で定義します。

## 12. UnitType
ユニットのタイプ（DEFENSE, SIEGE）を列挙型で定義します。

## 13. BuildingButton / UnitBUtton
建物やユニットを選択するためのボタンを表します。
クリックされると、GamePanelにアクションを通知します。
クラス間の関係性
GameMainはGameとGamePanelを初期化し、プレイヤーやボットを作成。
Gameはプレイヤー（Player）、ボット、ユニット（Unit）、建物（Building）、プロジェクタイル（Projectile）を管理。
各プレイヤーは、城（Castle）、ユニット（Unit）、建物（Building）を所有。
ユニットは抽象クラスUnitを基にし、攻城ユニット（SiegeUnit）、防衛ユニット（DefenseUnit）として具象化。
建物は抽象クラスBuildingを基に、資源生成建物（ResourceBuilding）、防衛建物（DefenseBuilding）として具象化。
GamePanelとSelectionPanelはプレイヤーの操作を受け取り、ゲームの状態を変更。
ボット（Bot）はAIとして自動的にユニットや建物を管理。
