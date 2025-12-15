package xzero.model.labels;

import xzero.model.Player;

public class NormalLabel extends Label {

    private final Player _owner;

    /**
     * Создания обычной метки, принадлежащей конкретному игроку
     *
     * @param owner - владелец метки
     * @throws IllegalArgumentException если владелец равен null
     */
    public NormalLabel(Player owner) {
        if (owner == null) {
            throw new IllegalArgumentException("NormalLabel: owner не может быть null");
        }

        _owner = owner;
    }

    /**
     * Получение логического владельца метки
     *
     * @return владелец метки
     */
    @Override
    public Player owner() {
        return _owner;
    }

    /**
     * Получение символа отображения
     *
     * @return символ метки
     */
    @Override
    public String symbol() {
        return _owner.name();
    }
}
