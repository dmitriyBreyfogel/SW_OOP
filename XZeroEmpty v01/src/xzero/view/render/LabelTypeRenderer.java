package xzero.view.render;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import xzero.model.labels.LabelType;

/**
 * Рендерер элементов выпадающего списка для отображения типов меток в человекочитаемом виде
 */
public class LabelTypeRenderer extends DefaultListCellRenderer {

    /**
     * Формирует визуальное представление элемента списка типов меток
     *
     * @param list список, для которого выполняется рендеринг
     * @param value значение элемента списка
     * @param index индекс элемента
     * @param isSelected признак выбранного элемента
     * @param cellHasFocus признак фокуса элемента
     * @return компонент для отображения элемента списка
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
     * Преобразует тип метки в человекочитаемое строковое представление
     *
     * @param type тип метки
     * @return строка для отображения пользователю
     */
    private String toDisplayName(LabelType type) {
        return switch (type) {
            case NORMAL -> "Текущая";
            case DELEGATED -> "Метка противника";
            case HIDDEN -> "Скрытая";
        };
    }
}
