package xzero.view;

import java.awt.Point;
import javax.swing.JButton;

/**
 * Кнопка-ячейка игрового поля с сохранённой позицией на поле.
 */
public class CellButton extends JButton {

    private final Point position;

    public CellButton(Point position) {
        super("");
        this.position = position;
        setFocusable(false);
    }

    public Point position() {
        return position;
    }
}
