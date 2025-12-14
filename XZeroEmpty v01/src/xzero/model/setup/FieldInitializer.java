package xzero.model.setup;

import xzero.model.GameField;
import xzero.model.factory.CellFactory;

/**
 * Стратегия подготовки игрового поля.
 */
public interface FieldInitializer {
    void prepare(GameField field, CellFactory cellFactory);
}

