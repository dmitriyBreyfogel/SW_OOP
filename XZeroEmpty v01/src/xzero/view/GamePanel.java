package xzero.view;

import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Box;

import xzero.model.GameModel;
import xzero.model.Player;
import xzero.model.events.GameEvent;
import xzero.model.events.GameListener;
import xzero.model.events.PlayerActionEvent;
import xzero.model.events.PlayerActionListener;
import xzero.model.labels.Label;
import xzero.model.labels.LabelType;

/**
 * Главное окно приложения, связывающее пользовательский интерфейс с моделью игры
 */
public class GamePanel extends JFrame {

    private final GameModel model = new GameModel();
    private final FieldPanel fieldPanel = new FieldPanel(model, this::handleCellClick);
    private final InfoPanel infoPanel = new InfoPanel(this::handleLabelTypeChange, this::handlePassRequest, this::handleSecretModeChange);

    /**
     * Создаёт главное окно игры, инициализируя интерфейс и подписки на события модели
     */
    public GamePanel() {
        super("Крестики-нолики NEXT");

        model.addGameListener(new GameObserver());
        model.addPlayerActionListener(new PlayerObserver());

        setJMenuBar(new GameMenu(this::startNewGame));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Box mainBox = Box.createVerticalBox();
        mainBox.add(Box.createVerticalStrut(10));
        mainBox.add(infoPanel);
        mainBox.add(Box.createVerticalStrut(10));

        fieldPanel.setDoubleBuffered(true);
        fieldPanel.buildField();
        fieldPanel.setInteractionEnabled(false);
        mainBox.add(fieldPanel);

        setContentPane(mainBox);
        pack();
        setResizable(false);
    }

    /**
     * Запускает новую игру и пересоздаёт отображение игрового поля
     */
    private void startNewGame() {
        model.start();
        infoPanel.setSecretMode(model.secretModeEnabled());
        fieldPanel.buildField();
    }

    /**
     * Обрабатывает клик по ячейке и передаёт действие активному игроку
     *
     * @param position позиция ячейки, по которой был выполнен клик
     */
    private void handleCellClick(Point position) {
        model.activePlayer().setLabelTo(position);
    }

    /**
     * Обрабатывает смену выбранного типа метки и передаёт выбор в модель
     *
     * @param labelType выбранный тип метки
     */
    private void handleLabelTypeChange(LabelType labelType) {
        model.setActiveLabelType(labelType);
    }

    /**
     * Обработчик переключения режима секретности
     *
     * @param enabled true ¢?" режим секретности включён, false ¢?" выключен
     */
    private void handleSecretModeChange(boolean enabled) {
        model.setSecretModeEnabled(enabled);
    }

    /**
     * Обрабатывает запрос на передачу хода и передаёт его в модель
     */
    private void handlePassRequest() {
        model.passTurn();
    }

    /**
     * Включает или отключает взаимодействие пользователя с элементами интерфейса
     *
     * @param enabled true — разрешить взаимодействие, false — запретить
     */
    private void setInteractionEnabled(boolean enabled) {
        fieldPanel.setInteractionEnabled(enabled);
        infoPanel.setInteractionEnabled(enabled);
    }

    /**
     * Внутренний слушатель действий игрока, обновляющий отображение меток и доступность интерфейса
     */
    private class PlayerObserver implements PlayerActionListener {

        /**
         * Обрабатывает событие установки метки игроком и обновляет игровое поле
         *
         * @param event событие действия игрока
         */
        @Override
        public void labelIsPlaced(PlayerActionEvent event) {
            drawLabelOnField(event.label());
            setInteractionEnabled(false);
        }

        /**
         * Обрабатывает событие получения метки игроком и обновляет информационную панель
         *
         * @param event событие действия игрока
         */
        @Override
        public void labelIsReceived(PlayerActionEvent event) {
            drawLabelOnInfoPanel(event.label());
            drawPassesOnInfoPanel(event.player());
            setInteractionEnabled(true);
        }
    }

    /**
     * Внутренний слушатель событий игры, обновляющий интерфейс и показывающий результат партии
     */
    private class GameObserver implements GameListener {

        /**
         * Обрабатывает событие завершения игры и отображает сообщение о победителе
         *
         * @param event событие завершения игры
         */
        @Override
        public void gameFinished(GameEvent event) {
            Player winner = event.player();
            if (winner != null) {
                String message = "Победил игрок '" + winner.name() + "' !!!";
                JOptionPane.showMessageDialog(null, message, "Победа!", JOptionPane.INFORMATION_MESSAGE);
                setInteractionEnabled(false);
            }
        }

        /**
         * Обрабатывает событие смены активного игрока и обновляет информационную панель
         *
         * @param event событие смены игрока
         */
        @Override
        public void playerExchanged(GameEvent event) {
            infoPanel.setSecretMode(model.secretModeEnabled());
            drawPlayerOnInfoPanel(event.player());
            drawPassesOnInfoPanel(event.player());
        }
    }

    /**
     * Отображает активного игрока на информационной панели
     *
     * @param player игрок, которого нужно показать
     */
    private void drawPlayerOnInfoPanel(Player player) {
        infoPanel.showPlayer(player);
    }

    /**
     * Отображает активную метку на информационной панели
     *
     * @param label метка, которую нужно показать
     */
    private void drawLabelOnInfoPanel(Label label) {
        infoPanel.showLabel(label);
    }

    /**
     * Отображает метку на игровом поле
     *
     * @param label метка, которую нужно отрисовать
     */
    private void drawLabelOnField(Label label) {
        fieldPanel.drawLabel(label);
    }

    /**
     * Отображает количество оставшихся пасов на информационной панели
     *
     * @param player игрок, для которого нужно показать количество пасов
     */
    private void drawPassesOnInfoPanel(Player player) {
        int passesLeft = model.passesLeftFor(player);
        infoPanel.showPasses(passesLeft);
    }
}
