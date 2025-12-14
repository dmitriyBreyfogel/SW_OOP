package xzero.model.factory;

import xzero.model.Player;
import xzero.model.labels.*;

/**
 * Фабрика, порождающая метки разных типов
 */
public class LabelFactory {

    /**
     * Создаёт метку указанного типа для заданного владельца
     *
     * @param owner логический владелец метки
     * @param type тип метки
     * @return созданная метка
     *
     * @throws IllegalArgumentException
     */
    public Label createLabel(Player owner, LabelType type) {
        if (owner == null) {
            throw new IllegalArgumentException("LabelFactory: owner не может быть null");
        }

        if (type == null) {
            throw new IllegalArgumentException("LabelFactory: type не может быть null");
        }

        switch (type) {
            case NORMAL:
                return new NormalLabel(owner);
            case DELEGATED:
                return new DelegatedLabel(owner);
            case HIDDEN:
                return new HiddenLabel(owner);
            default:
                throw new IllegalArgumentException("LabelFactory: неизвестный тип метки");
        }
    }

    /**
     * Создаёт метку для текущего игрока, учитывая выбранный тип метки и его противника
     *
     * @param currentPlayer активный игрок, совершающий ход
     * @param opponent его противник
     * @param type желаемый тип метки
     * @return созданная метка с корректным владельцем
     *
     * @throws IllegalArgumentException
     */
    public Label createLabel(Player currentPlayer, Player opponent, LabelType type) {
        if (type == LabelType.DELEGATED) {
            if (opponent == null) {
                throw new IllegalArgumentException("LabelFactory: opponent не может быть null для делегированной метки");
            }
            return createLabel(opponent, type);
        }

        return createLabel(currentPlayer, type);
    }
}
