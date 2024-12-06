public class GameField {
    private char[][] field;
    private Castle castle;

    public GameField(int width, int height) {
        field = new char[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                field[i][j] = '.';
            }
        }
    }

    public void placeCastle(Castle castle) {
        this.castle = castle;
        field[castle.getY()][castle.getX()] = 'C';
    }

    public boolean placeUnit(Unit unit) {
        int x = unit.getX();
        int y = unit.getY();

        if (x < 0 || x >= field[0].length || y < 0 || y >= field.length || field[y][x] != '.') {
            return false;
        }

        field[y][x] = 'U';
        return true;
    }

    public void display() {
        for (char[] row : field) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }
}
