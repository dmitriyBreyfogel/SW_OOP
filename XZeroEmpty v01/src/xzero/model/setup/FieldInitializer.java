package xzero.model.setup;

import xzero.model.GameField;
import xzero.model.factory.CellFactory;

/**
 * Стратегия подготовки и инициализации игрового поля
 */
public interface FieldInitializer {

    /**
     * Подготавливает игровое поле с использованием заданной фабрики ячеек
     *
     * @param field игровое поле, подлежащее инициализации
     * @param cellFactory фабрика для создания ячеек игрового поля
     */
    void prepare(GameField field, CellFactory cellFactory);
}

