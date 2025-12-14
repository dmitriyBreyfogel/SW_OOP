package xzero.model;


import xzero.model.labels.Label;

import java.awt.Point;

/**
 * Ячейка игрового поля, содержащая позицию и размещённую в ней метку
 */
public class Cell {
    // --------------------- Позиция метки -----------------------
    private Point _position;

    /**
     * Устанавливает позицию ячейки на игровом поле
     *
     * @param pos позиция ячейки
     *
     * @throws IllegalArgumentException если позиция равна null
     */
    public void setPosition(Point pos){
        if (pos == null) {
            throw new IllegalArgumentException("Позиция ячейки не может быть null");
        }
        _position = new Point(pos);
    }

    /**
     * Возвращает позицию ячейки на игровом поле
     *
     * @return позиция ячейки
     */
    public Point position(){
        return (Point)_position.clone();
    }

    // --------- Поле, которому принадлежит ячейка. Задает само поле --------------
    private GameField _field;

    /**
     * Устанавливает игровое поле, к которому принадлежит ячейка
     *
     * @param field игровое поле
     *
     * @throws IllegalArgumentException если поле равно null
     * @throws IllegalStateException если ячейка уже принадлежит другому полю
     */
    public void setField(GameField field){
        if (field == null) {
            throw new IllegalArgumentException("Поле ячейки не может быть null");
        }
        if (_field != null && _field != field) {
            throw new IllegalStateException("Ячейка уже принадлежит другому полю");
        }
        _field = field;
    }

    // --------------------- Метка, принадлежащая ячейке --------------------------
    private Label _label = null;

    /**
     * Помещает метку в ячейку с проверками и установкой двусторонней связи
     *
     * @param label метка, размещаемая в ячейке
     *
     * @throws IllegalArgumentException если метка равна null
     * @throws IllegalStateException если ячейка уже содержит метку
     * @throws IllegalStateException если метка уже размещена в другой ячейке
     */
    public void placeLabel(Label label) {
        if (label == null) {
            throw new IllegalArgumentException("Нельзя поместить null-метку в ячейку");
        }
        if (_label != null) {
            throw new IllegalStateException("Ячейка уже занята меткой");
        }
        if (label.cell() != null && label.cell() != this) {
            throw new IllegalStateException("Эта метка уже находится в другой ячейке");
        }

        _label = label;
        label.setCell(this);
    }

    /**
     * Удаляет метку из ячейки с разрывом двусторонней связи
     */
    public void removeLabel() {
        if (_label != null) {
            Label old = _label;
            _label = null;
            old.unsetCell();
        }
    }

    /**
     * Возвращает метку, размещённую в ячейке
     *
     * @return метка или null, если ячейка пуста
     */
    public Label label() {
        return _label;
    }

    /**
     * Проверяет, пуста ли ячейка
     *
     * @return true, если метка отсутствует, иначе false
     */
    public boolean isEmpty(){
        return _label == null;
    }
}
