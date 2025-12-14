package xzero.model.setup;

import java.awt.Point;

import xzero.model.GameField;
import xzero.model.factory.CellFactory;

/**
 * Подготавливает прямоугольную сетку фиксированного размера, создавая ячейки
 * через переданную фабрику.
 */
public class GridFieldInitializer implements FieldInitializer {

    private final int width;
    private final int height;

    public GridFieldInitializer(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Размеры поля должны быть положительными");
        }
        this.width = width;
        this.height = height;
    }

    @Override
    public void prepare(GameField field, CellFactory cellFactory) {
        field.clear();
        field.setSize(width, height);
        for (int row = 1; row <= field.height(); row++) {
            for (int col = 1; col <= field.width(); col++) {
                field.setCell(new Point(col, row), cellFactory.createCell());
            }
        }
    }
}

