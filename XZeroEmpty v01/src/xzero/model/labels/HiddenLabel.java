package xzero.model.labels;

import xzero.model.Player;

public class HiddenLabel extends Label {

    // Реальный владелец метки, скрытый от игроков до окончания игры
    private final Player _owner;

    /**
     * Создание скрытой метки
     */
    public HiddenLabel(Player owner) {
        if (owner == null) {
            throw new IllegalArgumentException("HiddenLabel: owner не может быть null");
        }

        _owner = owner;
    }

    /**
     * Получение реального владельца метки
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
        return "?";
    }
}
