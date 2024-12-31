import java.util.List;
import java.util.ArrayList;

public class Player {
  private String name;
  private int resources;
  private Castle castle;
  private List<Building> buildings;
  private List<Unit> units;

  public Player(String name, int initialResources) {
    this.name = name;
    this.resources = initialResources;
    buildings = new ArrayList<>();
    units = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public int getResources() {
    return resources;
  }

  public void addResources(int amount) {
    resources += amount;
  }

  public boolean spendResources(int amount) {
    if (resources >= amount) {
      resources -= amount;
      return true;
    }
    return false;
  }

  public Castle getCastle() {
    return castle;
  }

  public void setCastle(Castle castle) {
    this.castle = castle;
  }

  public List<Building> getBuildings() {
    return buildings;
  }

  public void addBuilding(Building building) {
    buildings.add(building);
  }

  public List<Unit> getUnits() {
    return units;
  }

  public void addUnit(Unit unit) {
    units.add(unit);
  }

  public void removeUnit(Unit unit) {
    units.remove(unit);
  }

  public void buildResource() {
    if (spendResources(100)) { // 資源設備のコスト
      Building resourceBuilding = new ResourceBuilding(0, 0, this); // 座標は仮設定
      addBuilding(resourceBuilding);
      System.out.println(name + " が資源設備を建設しました。");
    } else {
      System.out.println(name + " の資源が不足しています。");
    }
  }

  public void buildDefense() {
    if (spendResources(150)) { // 防衛設備のコスト
      Building defenseBuilding = new DefenseBuilding(0, 0, this); // 座標は仮設定
      addBuilding(defenseBuilding);
      System.out.println(name + " が防衛設備を建設しました。");
    } else {
      System.out.println(name + " の資源が不足しています。");
    }
  }

  public void deploySiege() {
    if (spendResources(200)) { // 攻城ユニットのコスト
      Unit siegeUnit = new SiegeUnit(0, 0, 1.0, 200, this); // 座標と速度は仮設定
      addUnit(siegeUnit);
      System.out.println(name + " が攻城ユニットを配備しました。");
    } else {
      System.out.println(name + " の資源が不足しています。");
    }
  }

  public void deployDefense() {
    if (spendResources(100)) { // 防衛ユニットのコスト
      Unit defenseUnit = new DefenseUnit(0, 0, this); // 座標は仮設定
      addUnit(defenseUnit);
      System.out.println(name + " が防衛ユニットを配備しました。");
    } else {
      System.out.println(name + " の資源が不足しています。");
    }
  }

  // ボットのためのメソッド
  public void upgradeBuilding(Building building) {
    if (building != null) {
      building.upgrade();
    }
  }
}
