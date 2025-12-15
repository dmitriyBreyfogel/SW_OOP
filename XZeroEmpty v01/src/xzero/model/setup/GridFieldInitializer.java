package xzero.model.setup;

import java.awt.Point;

import xzero.model.GameField;
import xzero.model.factory.CellFactory;

/**
 * Инициализатор игрового поля в виде прямоугольной сетки фиксированного размера
 */
public class GridFieldInitializer implements FieldInitializer {

    private final int width;
    private final int height;

    /**
     * Создаёт инициализатор с заданными размерами игрового поля
     *
     * @param width ширина
     * @param height высота
     *
     * @throws IllegalArgumentException если ширина или длина отрицательны
     */
    public GridFieldInitializer(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Размеры поля должны быть положительными");
        }
        this.width = width;
        this.height = height;
    }

    /**
     * Подготавливает игровое поле, очищая его и заполняя ячейками через фабрику
     *
     * @param field игровое поле, которое необходимо инициализировать
     * @param cellFactory фабрика для создания ячеек
     */
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

