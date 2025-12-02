package xzero.model;

import xzero.model.labels.Label;

import java.awt.Point;

/**
 * Ячейка, являющаяся составной частью поля и содержащая в себе метку
 */
public class Cell {
    // --------------------- Позиция метки -----------------------
    private Point _position;

    public void setPosition(Point pos){
        _position = pos;
    }

    public Point position(){
        return (Point)_position.clone();
    }

    // --------- Поле, которому принадлежит ячейка. Задает само поле --------------
    private GameField _field;

    public void setField(GameField f){
        _field = f;
    }

    // --------------------- Метка, принадлежащая ячейке --------------------------
    private xzero.model.labels.Label _label = null;

    /**
     * Поместить метку в ячейку с ранними проверками и двусторонней связью.
     */
    public void placeLabel(xzero.model.labels.Label l) {
        if (l == null) {
            throw new IllegalArgumentException("Нельзя поместить null-метку в ячейку");
        }
        if (_label != null) {
            throw new IllegalStateException("Ячейка уже занята меткой");
        }
        if (l.cell() != null && l.cell() != this) {
            throw new IllegalStateException("Эта метка уже находится в другой ячейке");
        }

        _label = l;
        l.setCell(this);
    }

    /**
     * Удалить метку из ячейки (если она есть) с двусторонним разрывом связи.
     */
    public void removeLabel() {
        if (_label != null) {
            xzero.model.labels.Label old = _label;
            _label = null;
            old.unsetCell();
        }
    }

    public Label label() {
        return _label;
    }

    public boolean isEmpty(){
        return _label == null;
    }
}
