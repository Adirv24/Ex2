
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ex2Sheet implements Sheet {
    private Cell[][] table;

    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
        eval();
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public String value(int x, int y) {
        if (!isIn(x, y)) return Ex2Utils.EMPTY_CELL;
        Cell c = get(x, y);
        return c != null ? c.getData() : Ex2Utils.EMPTY_CELL;
    }

    @Override
    public Cell get(int x, int y) {
        return isIn(x, y) ? table[x][y] : null;
    }

    @Override
    public Cell get(String cords) {
        try {
            int x = cords.charAt(0) - 'A';
            int y = Integer.parseInt(cords.substring(1)) - 1;
            return get(x, y);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int width() {
        return table.length;
    }

    @Override
    public int height() {
        return table[0].length;
    }

    @Override
    public void set(int x, int y, String s) {
        if (!isIn(x, y)) return;
        table[x][y] = new SCell(s);
        eval();
    }

    @Override
    public void eval() {
        int[][] depths = depth();
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                Cell c = table[x][y];
                if (c != null && c.getType() == Ex2Utils.FORM) {
                    String result = eval(x, y);
                    c.setData(result);
                }
            }
        }
    }

    @Override
    public boolean isIn(int x, int y) {
        return x >= 0 && x < width() && y >= 0 && y < height();
    }

    @Override
    public int[][] depth() {
        int[][] depths = new int[width()][height()];
        for (int[] row : depths) {
            Arrays.fill(row, -1);
        }

        boolean changed = true;
        int iteration = 0;

        while (changed && iteration < width() * height()) {
            changed = false;
            for (int x = 0; x < width(); x++) {
                for (int y = 0; y < height(); y++) {
                    if (depths[x][y] == -1) {
                        int dep = determineCellDepth(x, y, depths);
                        if (dep >= 0) {
                            depths[x][y] = dep;
                            changed = true;
                        }
                    }
                }
            }
            iteration++;
        }

        return depths;
    }

    private int determineCellDepth(int x, int y, int[][] depths) {
        if (!isIn(x, y)) return 0;

        Cell cell = table[x][y];
        if (cell == null || cell.getType() == Ex2Utils.TEXT || cell.getType() == Ex2Utils.NUMBER) {
            return 0;
        }

        if (cell.getType() == Ex2Utils.FORM) {
            Set<String> dependencies = getCellDependencies(cell.getData());
            int maxDepth = 0;

            for (String dep : dependencies) {
                Cell depCell = get(dep);
                if (depCell == null) return -1;

                int depDepth = depths[getColumnIndex(dep)][getRowIndex(dep)];
                if (depDepth == -1) return -1;

                maxDepth = Math.max(maxDepth, depDepth);
            }

            return maxDepth + 1;
        }

        return -1;
    }

    private Set<String> getCellDependencies(String formula) {
        Set<String> dependencies = new HashSet<>();
        String[] tokens = formula.substring(1).split("[+\\-*/()]");
        for (String token : tokens) {
            if (token.matches("[A-Za-z]+\\d+")) {
                dependencies.add(token);
            }
        }
        return dependencies;
    }

    private int getColumnIndex(String cords) {
        return cords.charAt(0) - 'A';
    }

    private int getRowIndex(String cords) {
        return Integer.parseInt(cords.substring(1)) - 1;
    }

    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length >= 3) {
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    set(x, y, parts[2]);
                }
            }
        }
    }

    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("I2CS ArielU: SpreadSheet (Ex2) assignment\n");
            for (int x = 0; x < width(); x++) {
                for (int y = 0; y < height(); y++) {
                    Cell cell = table[x][y];
                    if (cell != null && !cell.getData().equals(Ex2Utils.EMPTY_CELL)) {
                        writer.write(String.format("%d,%d,%s\n", x, y, cell.getData()));
                    }
                }
            }
        }
    }

    private String findAndUpdateCellReferences(String formula, int currentX, int currentY, Set<String> visited) {
        StringBuilder resolvedFormula = new StringBuilder();
        String regex = "[A-Za-z]+[0-9]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(formula);
        int lastIndex = 0;
        while (matcher.find()) {
            String ref = matcher.group();
            int x = getColumnIndex(ref);
            int y = getRowIndex(ref);
            resolvedFormula.append(formula, lastIndex, matcher.start());
            String cellKey = x + "," + y;
            if (visited.contains(cellKey)) {
                throw new IllegalArgumentException("Circular reference detected: " + ref);
            }
            visited.add(cellKey);
            if (x == currentX && y == currentY) {
                return String.valueOf(Ex2Utils.ERR_CYCLE_FORM);
            }
            if (isIn(x, y)) {
                Cell referencedCell = get(x, y);
                if (referencedCell == null || referencedCell.getData().isEmpty()) {
                    resolvedFormula.append("0");
                } else {
                    String value = eval(x, y, visited);
                    if (value.equals(String.valueOf(Ex2Utils.ERR_CYCLE_FORM))) {
                        return String.valueOf(Ex2Utils.ERR_CYCLE_FORM);
                    }
                    resolvedFormula.append(value);
                }
            } else {
                throw new IllegalArgumentException("Out of bounds cell reference: " + ref);
            }

            lastIndex = matcher.end();
            visited.remove(cellKey);
        }

        resolvedFormula.append(formula.substring(lastIndex));
        return resolvedFormula.toString();
    }

    @Override
    public String eval(int x, int y) {
        return eval(x, y, new HashSet<>());
    }

    private String eval(int x, int y, Set<String> visited) {
        if (!isIn(x, y)) return Ex2Utils.EMPTY_CELL;

        Cell cell = get(x, y);
        if (cell == null) return Ex2Utils.EMPTY_CELL;

        String data = cell.getData();
        if (cell.getType() == Ex2Utils.NUMBER || cell.getType() == Ex2Utils.TEXT) {
            return data;
        }

        if (cell.getType() == Ex2Utils.FORM) {
            try {
                String formula = data.substring(1); // Remove '='
                String resolvedFormula = findAndUpdateCellReferences(formula, x, y, visited);
                return Double.toString(SCell.computeFormula("=" + resolvedFormula));
            } catch (ArithmeticException e) {
                return String.valueOf(Ex2Utils.ERR_CYCLE_FORM);
            } catch (Exception e) {
                return String.valueOf("ERR_WRONG_FORM");
            }
        }

        return Ex2Utils.EMPTY_CELL;
    }



}
