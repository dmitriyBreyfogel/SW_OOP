package xzero.model;

import java.util.List;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

import xzero.model.labels.Label;
import xzero.model.navigation.Direction;
import xzero.model.navigation.Shift;

/**
 *  Прямоугольное поле, состоящее из ячеек
 */
public class GameField {
    // ------------------------------ Ячейки ---------------------------------------
    private ArrayList<Cell> _cellPool = new ArrayList();

    Cell cell(Point pos) {
        for(Cell obj : _cellPool) {
            if(obj.position().equals(pos))
            { return obj; }
        }

        return null;
    }

    public void setCell(Point pos, Cell cell) {
        removeCell(pos);

        cell.setField(this);
        cell.setPosition(pos);

        _cellPool.add(cell);
    }

    public void clear(){
        _cellPool.clear();
    }

    private void removeCell(Point pos){
        Cell obj = cell(pos);
        if(obj != null)     _cellPool.remove(obj);
    }

    // ------------------------------ Метки ---------------------------------------
    public xzero.model.labels.Label label(Point pos) {
        Cell obj = cell(pos);
        if(obj != null)     return obj.label();

        return null;
    }

    public void setLabel(Point pos, xzero.model.labels.Label label) {
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

    private ArrayList<xzero.model.labels.Label> _labelPool = new ArrayList();

    public List<xzero.model.labels.Label> labels() {
        _labelPool.clear();

        for(Cell obj : _cellPool) {
            xzero.model.labels.Label l = obj.label();
            if(l != null) {
                _labelPool.add(obj.label());
            }
        }

        return Collections.unmodifiableList(_labelPool);
    }

    public List<xzero.model.labels.Label> labelLine(Point start, Direction direct) {
        ArrayList<xzero.model.labels.Label> line = new ArrayList<>();
        boolean isLineFinished = false;
        Player startPlayer = null;

        Point pos = new Point(start);
        Label l = label(pos);

        isLineFinished = (l == null);
        if(!isLineFinished) {
            line.add(l);
            startPlayer = line.get(0).player();
        }

        Shift shift = direct.shift();
        pos.translate(shift.byHorizontal(), shift.byVertical());
        while(!isLineFinished && containsRange(pos)) {
            l = label(pos);
            isLineFinished = (l == null || !l.player().equals(startPlayer));

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

        _cellPool.removeIf(c -> !containsRange(c.position()));
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
        setSize(10, 10);
    }
}
