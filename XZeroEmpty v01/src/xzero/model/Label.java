package xzero.model;

/**
 * Метка, которую можно поместить на поле
 */
public class Label {
    // --------- Ячейка, которой принадлежит метка (может быть null) ---------
    private Cell _cell = null;

    /** Пакетный: назначается только из Cell при установке метки в ячейку. */
    public void setCell(Cell c) {
        _cell = c;
    }

    /** Пакетный: сбрасывается только из Cell при удалении метки из ячейки. */
    public void unsetCell() {
        _cell = null;
    }

    /** Публично — только чтение места расположения метки. */
    public Cell cell() {
        return _cell;
    }

    // --------- Игрок, которому принадлежит метка (может быть нейтральной) ---
    private Player _player = null;

    /** Пакетный: назначается из Player при выдаче активной метки. */
    public void setPlayer(Player p) {
        _player = p;
    }

    /** Пакетный: сбрасывается из Player, если метка изымается. */
    public void unsetPlayer() {
        _player = null;
    }

    /** Публично — только чтение владельца метки. */
    public Player player() {
        return _player;
    }
}
