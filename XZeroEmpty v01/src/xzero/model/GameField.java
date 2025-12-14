package xzero.model;

import java.util.List;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import xzero.model.labels.Label;
import xzero.model.navigation.Direction;
import xzero.model.navigation.Shift;

/**
 *  Прямоугольное поле, состоящее из ячеек
 */
public class GameField {
    // ------------------------------ Ячейки ---------------------------------------
    private final Map<Point, Cell> _cellPool = new HashMap<>();

    Cell cell(Point pos) {
        if (pos == null) {
            return null;
        }
        return _cellPool.get(new Point(pos));
    }

    public void setCell(Point pos, Cell cell) {
        if (pos == null) {
            throw new IllegalArgumentException("Позиция ячейки не может быть null");
        }
        if (cell == null) {
            throw new IllegalArgumentException("Ячейка не может быть null");
        }

        Point safePoint = new Point(pos);

        removeCell(safePoint);

        cell.setField(this);
        cell.setPosition(safePoint);

        _cellPool.put(safePoint, cell);
    }

    public void clear(){
        _cellPool.clear();
    }

    private void removeCell(Point pos){
        if (pos == null) {
            return;
        }
        _cellPool.remove(new Point(pos));
    }

    // ------------------------------ Метки ---------------------------------------
    public Label label(Point pos) {
        Cell obj = cell(pos);
        if(obj != null)     return obj.label();

        return null;
    }

    public void setLabel(Point pos, Label label) {
        if (label == null) {
            throw new IllegalArgumentException("Нельзя установить null-метку");
        }
        if (!containsRange(pos)) {
            throw new IndexOutOfBoundsException("Позиция вне поля: " + pos);
        }
        Cell obj = cell(pos);
        if (obj == null) {
            throw new IllegalStateException("Ячейка по позиции " + pos + " не создана");
        }
        if (label.cell() != null && label.cell() != obj) {
            throw new IllegalStateException("Эта метка уже установлена в другой ячейке");
        }
        obj.placeLabel(label);
    }

    private ArrayList<Label> _labelPool = new ArrayList<>();

    public List<Label> labels() {
        _labelPool.clear();

        for(Cell obj : _cellPool.values()) {
            Label l = obj.label();
            if(l != null) {
                _labelPool.add(obj.label());
            }
        }

        return Collections.unmodifiableList(_labelPool);
    }

    public List<Label> labelLine(Point start, Direction direct) {
        ArrayList<Label> line = new ArrayList<>();
        boolean isLineFinished = false;
        Player startPlayer = null;

        Point pos = new Point(start);
        Label l = label(pos);

        isLineFinished = (l == null);
        if(!isLineFinished) {
            line.add(l);
            startPlayer = line.get(0).owner();
        }

        Shift shift = direct.shift();
        pos.translate(shift.byHorizontal(), shift.byVertical());
        while(!isLineFinished && containsRange(pos)) {
            l = label(pos);
            isLineFinished = (l == null || !l.owner().equals(startPlayer));

            if(!isLineFinished) {
                line.add(l);
            }

            pos.translate(shift.byHorizontal(), shift.byVertical());
        }

        return line;
    }

    // ----------------------- Ширина и высота поля ------------------------------
    private int _width;
    private int _height;

    public void setSize(int width, int height) {
        _width = width;
        _height = height;

        _cellPool.entrySet().removeIf(entry -> !containsRange(entry.getKey()));
    }

    public int width() {
        return _width;
    }

    public int height() {
        return _height;
    }

    public boolean containsRange(Point p) {
        return p.getX() >= 1 && p.getX() <= _width &&
                p.getY() >= 1 && p.getY() <= _height ;
    }

    // ----------------------------------------------------------------------------
    public GameField() {
        setSize(5, 5);
    }
}
