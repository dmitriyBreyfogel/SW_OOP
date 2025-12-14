package xzero.view;

import java.awt.Point;
import javax.swing.JButton;

/**
 * Кнопка игрового поля, связанная с конкретной позицией ячейки
 */
public class CellButton extends JButton {

    private final Point position;

    /**
     * Создаёт кнопку ячейки с заданной позицией на игровом поле
     *
     * @param position позиция ячейки, связанная с кнопкой
     */
    public CellButton(Point position) {
        super("");
        this.position = position;
        setFocusable(false);
    }

    /**
     * Возвращает позицию ячейки, связанной с кнопкой
     *
     * @return позиция ячейки
     */
    public Point position() {
        return position;
    }
}
