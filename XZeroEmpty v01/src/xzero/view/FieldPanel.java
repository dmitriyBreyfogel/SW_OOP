package xzero.view;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
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
    private final Map<Point, CellButton> buttons = new HashMap<>();

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

        buttons.clear();

        setLayout(new GridLayout(model.field().height(), model.field().width()));
        Dimension fieldDimension = new Dimension(
                CELL_SIZE * model.field().width(),
                CELL_SIZE * model.field().height());
      
        setPreferredSize(fieldDimension);
        setMinimumSize(fieldDimension);
        setMaximumSize(fieldDimension);

        for (int row = 1; row <= model.field().height(); row++) {
            for (int col = 1; col <= model.field().width(); col++) {
                Point position = new Point(col, row);
                CellButton button = createCellButton(position);
                buttons.put(position, button);
                add(button);
            }
        }

        validate();
    }

    /**
     * Перерисовывает метку в указанной позиции.
     */
    public void drawLabel(Label label) {
        CellButton button = buttons.get(label.cell().position());
        if (button == null) {
            return;
        }
        button.setText(label.symbol());
    }

    /**
     * Управляет доступностью ячеек в зависимости от занятости.
     */
    public void setInteractionEnabled(boolean enabled) {
        for (Map.Entry<Point, CellButton> entry : buttons.entrySet()) {
            CellButton button = entry.getValue();

            if (!enabled) {
                button.setEnabled(false);
                continue;
            }

            Point position = entry.getKey();
            boolean isCellEmpty = model.field().label(position) == null;
            button.setEnabled(isCellEmpty);
        }
    }

    private CellButton createCellButton(Point position) {
        CellButton button = new CellButton(position);
        ActionListener listener = e -> handleCellClick(button);
        button.addActionListener(listener);
        return button;
    }

    private void handleCellClick(CellButton button) {
        button.setEnabled(false);
        onCellClicked.accept(button.position());
    }
}
