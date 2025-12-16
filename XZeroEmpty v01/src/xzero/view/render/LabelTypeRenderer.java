package xzero.view.render;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import xzero.model.labels.LabelType;

/**
 * Рендерер для отображения читабельных имён типов меток.
 */
public class LabelTypeRenderer extends DefaultListCellRenderer {

    /**
     * Возвращает компонент списка с локализованным названием типа.
     *
     * @param list список, в котором рендерится элемент
     * @param value значение элемента
     * @param index индекс
     * @param isSelected флаг выбранности
     * @param cellHasFocus флаг фокуса
     * @return компонент ячейки
     */
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof LabelType) {
            setText(toDisplayName((LabelType) value));
        }
        return this;
    }

    /**
     * Возвращает русское имя типа метки.
     *
     * @param type тип метки
     * @return отображаемое имя
     */
    private String toDisplayName(LabelType type) {
        return switch (type) {
            case NORMAL -> "Обычная";
            case DELEGATED -> "Делегированная";
            case HIDDEN -> "Скрытая";
        };
    }
}
