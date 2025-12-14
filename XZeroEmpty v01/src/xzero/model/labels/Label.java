package xzero.model.labels;

import xzero.model.Cell;
import xzero.model.Player;

public abstract class Label {

    // Ячейка, которой принадлежит метка
    private Cell _cell = null;

    public void setCell(Cell cell) {
        _cell = cell;
    }

    public void unsetCell() {
        _cell = null;
    }

    public Cell cell() {
        return _cell;
    }

    // Игрок, который поставил метку
    private Player _placedBy = null;

    public void setPlacedBy(Player p) {
        _placedBy = p;
    }

    public void unsetPlacedBy() {
        _placedBy = null;
    }

    public Player getPlacedBy() {
        return _placedBy;
    }

    /**
     * Определяет логического владельца метки
     */
    public abstract Player owner();

    /**
     * Символ, который нужно отображать
     */
    public abstract String symbol();
}
