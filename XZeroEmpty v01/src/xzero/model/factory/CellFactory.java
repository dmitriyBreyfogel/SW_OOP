package xzero.model.factory;

import xzero.model.Cell;

/**
 * Фабрика для создания ячеек игрового поля
 */
public class CellFactory {

    /**
     * Создаёт новую ячейку игрового поля
     *
     * @return новая ячейка
     */
    public Cell createCell(){
       return new Cell(); 
    }
}
