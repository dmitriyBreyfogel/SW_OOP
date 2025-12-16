package xzero.view;

import java.awt.Component;
import java.awt.Dimension;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import xzero.model.Player;
import xzero.model.labels.DelegatedLabel;
import xzero.model.labels.HiddenLabel;
import xzero.model.labels.Label;
import xzero.model.labels.LabelType;
import xzero.model.labels.SecretLabel;
import xzero.view.render.LabelTypeRenderer;

/**
 * Панель информации о текущем игроке, выбранной метке и пасах с поддержкой секретности.
 */
public class InfoPanel extends JPanel {

    private static final int CELL_SIZE = 50;

    private final JButton labelInfo = new JButton();
    private final JLabel playerInfo = new JLabel();
    private final JLabel passInfo = new JLabel();
    private final JButton passButton = new JButton("Пас");
    private final JComboBox<LabelType> labelTypeSelector = new JComboBox<>(LabelType.values());
    private final JCheckBox secretModeCheckbox = new JCheckBox("Секретно");

    private final Consumer<LabelType> onLabelTypeChanged;
    private final Runnable onPassRequested;
    private final Consumer<Boolean> onSecretModeChanged;

    private boolean adjustingSelector = false;
    private boolean secretModeEnabled = false;
    private boolean interactionEnabled = false;

    /**
     * Создаёт панель с обработчиками событий.
     *
     * @param onLabelTypeChanged обработчик смены типа метки
     * @param onPassRequested обработчик паса
     * @param onSecretModeChanged обработчик переключения секретности
     */
    public InfoPanel(Consumer<LabelType> onLabelTypeChanged, Runnable onPassRequested,
                     Consumer<Boolean> onSecretModeChanged) {
        this.onLabelTypeChanged = onLabelTypeChanged;
        this.onPassRequested = onPassRequested;
        this.onSecretModeChanged = onSecretModeChanged;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        buildContent();
    }

    /**
     * Показывает активного игрока.
     *
     * @param player активный игрок
     */
    public void showPlayer(Player player) {
        playerInfo.setText(player.name());
    }

    /**
     * Показывает количество оставшихся пасов.
     *
     * @param passesLeft число пасов
     */
    public void showPasses(int passesLeft) {
        passInfo.setText(String.format("Пасов: %d", passesLeft));
    }

    /**
     * Показывает активную метку игрока.
     *
     * @param label метка
     */
    public void showLabel(Label label) {
        labelInfo.setText(label.symbol());
        updateLabelSelector(label);
    }

    /**
     * Включает/выключает интерактивность элементов.
     *
     * @param enabled true ¢?" элементы активны
     */
    public void setInteractionEnabled(boolean enabled) {
        interactionEnabled = enabled;
        passButton.setEnabled(enabled);
        secretModeCheckbox.setEnabled(enabled);
        labelTypeSelector.setEnabled(enabled && !secretModeEnabled);
    }

    /**
     * Устанавливает состояние режима секретности.
     *
     * @param enabled true ¢?" включён
     */
    public void setSecretMode(boolean enabled) {
        secretModeEnabled = enabled;
        secretModeCheckbox.setSelected(enabled);
        labelTypeSelector.setEnabled(interactionEnabled && !enabled);
        labelTypeSelector.setRenderer(enabled ? new MaskedRenderer() : new LabelTypeRenderer());
        labelTypeSelector.setToolTipText(enabled ? "Тип метки скрыт" : null);
    }

    /**
     * Строит элементы панели.
     */
    private void buildContent() {
        add(Box.createHorizontalStrut(10));

        add(new JLabel("Игрок:"));
        playerInfo.setText("?");
        add(Box.createHorizontalStrut(10));
        add(playerInfo);

        add(Box.createHorizontalStrut(20));

        add(new JLabel("Метка:"));
        add(Box.createHorizontalStrut(10));

        labelInfo.setEnabled(false);
        labelInfo.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        labelInfo.setMinimumSize(new Dimension(CELL_SIZE, CELL_SIZE));
        labelInfo.setMaximumSize(new Dimension(CELL_SIZE, CELL_SIZE));
        add(labelInfo);

        add(Box.createHorizontalStrut(10));

        secretModeCheckbox.setFocusable(false);
        secretModeCheckbox.addActionListener(e -> onSecretModeCheckboxChanged());
        add(secretModeCheckbox);

        add(Box.createHorizontalStrut(10));
        add(new JLabel("Тип метки:"));
        add(Box.createHorizontalStrut(10));
        labelTypeSelector.setFocusable(false);
        labelTypeSelector.setRenderer(new LabelTypeRenderer());
        labelTypeSelector.addActionListener(e -> onLabelTypeSelectorChanged());
        add(labelTypeSelector);

        add(Box.createHorizontalStrut(10));

        passInfo.setHorizontalAlignment(SwingConstants.CENTER);
        passInfo.setText("Пасов: 0");
        passInfo.setPreferredSize(new Dimension(100, CELL_SIZE));
        add(passInfo);

        passButton.setText("Пас");
        passButton.setFocusable(false);
        passButton.addActionListener(e -> onPassButtonClicked());
        add(passButton);
    }

    /**
     * Обработчик смены типа метки.
     */
    private void onLabelTypeSelectorChanged() {
        if (adjustingSelector || secretModeEnabled) {
            return;
        }
        LabelType selectedType = (LabelType) labelTypeSelector.getSelectedItem();
        try {
            onLabelTypeChanged.accept(selectedType);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка выбора метки", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Обработчик переключения секретности.
     */
    private void onSecretModeCheckboxChanged() {
        boolean enabled = secretModeCheckbox.isSelected();
        try {
            onSecretModeChanged.accept(enabled);
            setSecretMode(enabled);
        } catch (RuntimeException ex) {
            secretModeCheckbox.setSelected(!enabled);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка переключения режима", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Обработчик нажатия паса.
     */
    private void onPassButtonClicked() {
        try {
            onPassRequested.run();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Пас недоступен", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Обновляет выбор типа без раскрытия скрытой метки.
     *
     * @param label метка
     */
    private void updateLabelSelector(Label label) {
        if (secretModeEnabled || label instanceof SecretLabel) {
            return;
        }
        adjustingSelector = true;
        labelTypeSelector.setSelectedItem(resolveLabelType(label));
        adjustingSelector = false;
    }

    /**
     * Определяет тип метки по её классу.
     *
     * @param label метка
     * @return тип метки
     */
    private LabelType resolveLabelType(Label label) {
        if (label instanceof HiddenLabel) {
            return LabelType.HIDDEN;
        }
        if (label instanceof DelegatedLabel) {
            return LabelType.DELEGATED;
        }
        return LabelType.NORMAL;
    }

    /**
     * Рендерер, маскирующий значение при секретности.
     */
    private static final class MaskedRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            setText("Тип скрыт");
            return this;
        }
    }
}
