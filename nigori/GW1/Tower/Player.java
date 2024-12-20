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

  public void setResources(int resources) {
    this.resources = resources;
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

  // ボットのためのメソッド
  public void upgradeBuilding(Building building) {
    if (building != null) {
      building.upgrade();
    }
  }
}
