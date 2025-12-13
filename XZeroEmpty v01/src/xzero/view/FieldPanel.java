package xzero.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import javax.swing.JButton;
import javax.swing.JPanel;

import xzero.model.GameModel;
import xzero.model.labels.Label;

/**
 * Панель игрового поля, отвечающая за отображение ячеек и обработку кликов по ним.
 */
public class FieldPanel extends JPanel {

    private static final int CELL_SIZE = 50;

    private final GameModel model;
    private final Consumer<Point> onCellClicked;

    public FieldPanel(GameModel model, Consumer<Point> onCellClicked) {
        this.model = model;
        this.onCellClicked = onCellClicked;
        setDoubleBuffered(true);
    }

    /**
     * Создаёт или пересоздаёт сетку кнопок на поле.
     */
    public void buildField() {
        removeAll();

        setLayout(new GridLayout(model.field().height(), model.field().width()));
        Dimension fieldDimension = new Dimension(
                CELL_SIZE * model.field().height(),
                CELL_SIZE * model.field().width());
        setPreferredSize(fieldDimension);
        setMinimumSize(fieldDimension);
        setMaximumSize(fieldDimension);

        for (int row = 1; row <= model.field().height(); row++) {
            for (int col = 1; col <= model.field().width(); col++) {
                JButton button = createCellButton();
                add(button);
            }
        }

        validate();
    }

    /**
     * Перерисовывает метку в указанной позиции.
     */
    public void drawLabel(Label label) {
        JButton button = buttonAt(label.cell().position());
        if (button != null) {
            button.setText(label.symbol());
        }
    }

    /**
     * Управляет доступностью ячеек в зависимости от занятости.
     */
    public void setInteractionEnabled(boolean enabled) {
        int width = model.field().width();
        Component[] components = getComponents();

        for (int index = 0; index < components.length; index++) {
            if (!(components[index] instanceof JButton)) {
                continue;
            }

            JButton button = (JButton) components[index];
            if (!enabled) {
                button.setEnabled(false);
                continue;
            }

            Point position = positionAt(index, width);
            boolean isCellEmpty = model.field().label(position) == null;
            button.setEnabled(isCellEmpty);
        }
    }

    private JButton createCellButton() {
        JButton button = new JButton("");
        button.setFocusable(false);
        ActionListener listener = e -> handleCellClick(button);
        button.addActionListener(listener);
        return button;
    }

    private void handleCellClick(JButton button) {
        button.setEnabled(false);
        Point position = buttonPosition(button);
        onCellClicked.accept(position);
    }

    private JButton buttonAt(Point pos) {
        int index = model.field().width() * (pos.y - 1) + (pos.x - 1);
        Component component = getComponent(index);
        if (component instanceof JButton) {
            return (JButton) component;
        }
        return null;
    }

    private Point buttonPosition(JButton button) {
        Component[] components = getComponents();
        int width = model.field().width();
        for (int index = 0; index < components.length; index++) {
            if (components[index].equals(button)) {
                return positionAt(index, width);
            }
        }
        return new Point(1, 1);
    }

    private Point positionAt(int index, int width) {
        int row = index / width;
        int col = index % width;
        return new Point(col + 1, row + 1);
    }
}
