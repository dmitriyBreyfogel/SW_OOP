package xzero.model.labels;

import xzero.model.Player;

/**
 * Класс скрытой метки
 */
public class HiddenLabel extends Label {

    private final Player _owner;    // Реальный владелец метки, скрытый от игроков до окончания игры

    /**
     * Создание скрытой метки
     *
     * @param owner владелец данной метки
     * @throws IllegalArgumentException
     */
    public HiddenLabel(Player owner) {
        if (owner == null) {
            throw new IllegalArgumentException("HiddenLabel: owner не может быть null");
        }

        _owner = owner;
    }

    /**
     * Получение реального владельца метки
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
     * @return символ данной метки
     */
    @Override
    public String symbol() {
        return "?";
    }
}
