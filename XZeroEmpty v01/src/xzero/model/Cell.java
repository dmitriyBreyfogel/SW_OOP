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
        if (pos == null) {
            throw new IllegalArgumentException("Позиция ячейки не может быть null");
        }
        _position = new Point(pos);
    }

    public Point position(){
        return (Point)_position.clone();
    }

    // --------- Поле, которому принадлежит ячейка. Задает само поле --------------
    private GameField _field;

    public void setField(GameField f){
        if (f == null) {
            throw new IllegalArgumentException("Поле ячейки не может быть null");
        }
        if (_field != null && _field != f) {
            throw new IllegalStateException("Ячейка уже принадлежит другому полю");
        }
        _field = f;
    }

    // --------------------- Метка, принадлежащая ячейке --------------------------
    private Label _label = null;

    /**
     * Поместить метку в ячейку с ранними проверками и двусторонней связью.
     */
    public void placeLabel(Label l) {
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
            Label old = _label;
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
