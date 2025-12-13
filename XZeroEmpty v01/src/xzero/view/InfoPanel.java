package xzero.view;

import java.awt.Dimension;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import xzero.model.Player;
import xzero.model.labels.DelegatedLabel;
import xzero.model.labels.HiddenLabel;
import xzero.model.labels.Label;
import xzero.model.labels.LabelType;

/**
 * Информационная панель: показывает текущего игрока, активную метку и позволяет
 * выбрать тип метки или выполнить пас.
 */
public class InfoPanel extends JPanel {

    private static final int CELL_SIZE = 50;

    private final JButton labelInfo = new JButton();
    private final JLabel playerInfo = new JLabel();
    private final JButton passButton = new JButton("Пас");
    private final JComboBox<LabelType> labelTypeSelector = new JComboBox<>(LabelType.values());

    private final Consumer<LabelType> onLabelTypeChanged;
    private final Runnable onPassRequested;

    private boolean adjustingSelector = false;

    public InfoPanel(Consumer<LabelType> onLabelTypeChanged, Runnable onPassRequested) {
        this.onLabelTypeChanged = onLabelTypeChanged;
        this.onPassRequested = onPassRequested;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        buildContent();
    }

    /**
     * Отображает имя активного игрока.
     */
    public void showPlayer(Player player) {
        playerInfo.setText(player.name());
    }

    /**
     * Отображает символ активной метки и синхронизирует выбор типа метки.
     */
    public void showLabel(Label label) {
        labelInfo.setText(label.symbol());
        updateLabelSelector(label);
    }

    /**
     * Управляет доступностью выбора метки и кнопки паса.
     */
    public void setInteractionEnabled(boolean enabled) {
        passButton.setEnabled(enabled);
        labelTypeSelector.setEnabled(enabled);
    }

    private void buildContent() {
        add(Box.createHorizontalStrut(10));

        add(new JLabel("Игрок :"));
        playerInfo.setText("?");
        add(Box.createHorizontalStrut(10));
        add(playerInfo);

        add(Box.createHorizontalStrut(20));

        add(new JLabel("Метка :"));
        add(Box.createHorizontalStrut(10));

        labelInfo.setEnabled(false);
        labelInfo.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        labelInfo.setMinimumSize(new Dimension(CELL_SIZE, CELL_SIZE));
        labelInfo.setMaximumSize(new Dimension(CELL_SIZE, CELL_SIZE));
        add(labelInfo);

        add(Box.createHorizontalStrut(10));
        add(new JLabel("Тип метки:"));
        add(Box.createHorizontalStrut(10));
        labelTypeSelector.setFocusable(false);
        labelTypeSelector.addActionListener(e -> onLabelTypeSelectorChanged());
        add(labelTypeSelector);

        add(Box.createHorizontalStrut(10));

        passButton.setFocusable(false);
        passButton.addActionListener(e -> onPassButtonClicked());
        add(passButton);
    }

    private void onLabelTypeSelectorChanged() {
        if (adjustingSelector) {
            return;
        }
        LabelType selectedType = (LabelType) labelTypeSelector.getSelectedItem();
        try {
            onLabelTypeChanged.accept(selectedType);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Выбор метки", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onPassButtonClicked() {
        try {
            onPassRequested.run();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Нельзя выполнить пас", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateLabelSelector(Label label) {
        adjustingSelector = true;
        labelTypeSelector.setSelectedItem(resolveLabelType(label));
        adjustingSelector = false;
    }

    private LabelType resolveLabelType(Label label) {
        if (label instanceof HiddenLabel) {
            return LabelType.HIDDEN;
        }
        if (label instanceof DelegatedLabel) {
            return LabelType.DELEGATED;
        }
        return LabelType.NORMAL;
    }
}
