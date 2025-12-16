package xzero.model.labels;

import xzero.model.Player;

/**
 * Секретная метка, скрывающая свой реальный вид до раскрытия.
 */
public class SecretLabel extends Label {

    private final Label _hidden; // Реальная метка, которую нужно скрыть до применения

    /**
     * Создаёт секретную метку, оборачивающую реальную.
     *
     * @param hidden реальная метка, скрываемая до раскрытия
     *
     * @throws IllegalArgumentException если скрываемая метка равна null
     */
    public SecretLabel(Label hidden) {
        if (hidden == null) {
            throw new IllegalArgumentException("SecretLabel: скрываемая метка не может быть null");
        }
        _hidden = hidden;
    }

    /**
     * Возвращает владельца скрытой метки.
     *
     * @return владелец скрытой метки
     */
    @Override
    public Player owner() {
        return _hidden.owner();
    }

    /**
     * Символ секретной метки, одинаковый для всех скрытых видов.
     *
     * @return символ секретной метки
     */
    @Override
    public String symbol() {
        return "*";
    }

    /**
     * Раскрывает скрытую метку.
     *
     * @return реальная метка
     */
    public Label reveal() {
        return _hidden;
    }
}
