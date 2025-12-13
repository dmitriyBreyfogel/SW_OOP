package xzero.view.render;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import xzero.model.labels.LabelType;

/**
 * Отдельный рендерер для выпадающего списка типов меток с человекочитаемыми
 * названиями.
 */
public class LabelTypeRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof LabelType) {
            setText(toDisplayName((LabelType) value));
        }
        return this;
    }

    private String toDisplayName(LabelType type) {
        return switch (type) {
            case NORMAL -> "Текущая";
            case DELEGATED -> "Метка противника";
            case HIDDEN -> "Скрытая";
        };
    }
}
