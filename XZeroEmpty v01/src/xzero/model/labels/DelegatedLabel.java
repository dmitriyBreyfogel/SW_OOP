package xzero.model.labels;

import xzero.model.Player;

/**
 * Класс делегированной метки
 */
public class DelegatedLabel extends Label {

    private final Player _owner;    // Логический владелец метки

    /**
     * Создание делегированной метки
     *
     * @param owner логический владелец метки
     * @throws IllegalArgumentException если владелец равен null
     */
    public DelegatedLabel(Player owner) {
        if (owner == null) {
            throw new IllegalArgumentException("DelegatedLabel: owner не может быть null");
        }

        _owner = owner;
    }

    /**
     * Получение логического владельца метки
     *
     * @return логический владелец метки
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
