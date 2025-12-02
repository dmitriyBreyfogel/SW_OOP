package xzero.model.labels;

import xzero.model.Player;

public class DelegatedLabel extends Label {
    // Логический владелец метки
    private final Player _owner;

    /**
     * Создание делегированной метки
     */
    public DelegatedLabel(Player owner) {
        if (owner == null) {
            throw new IllegalArgumentException("DelegatedLabel: owner не может быть null");
        }

        _owner = owner;
    }

    /**
     * Получение логического владельца метки
     */
    @Override
    public Player owner() {
        return _owner;
    }

    /**
     * Получение символа отображения
     */
    @Override
    public String symbol() {
        return _owner.name();
    }
}
