package xzero.view;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import xzero.model.labels.DelegatedLabel;
import xzero.model.labels.HiddenLabel;

/**
 * Главное окно приложения: собирает панель информации, поле и меню, подписывается
 * на события модели и передаёт пользовательские действия в модель.
 */
public class GamePanel extends JFrame {

    private final GameModel model = new GameModel();
    private final FieldPanel fieldPanel = new FieldPanel(model, this::handleCellClick);
    private final InfoPanel infoPanel = new InfoPanel(this::handleLabelTypeChange, this::handlePassRequest);

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
  
    private void startNewGame() {
        model.start();
        fieldPanel.buildField();
    }

    private void handleCellClick(Point position) {
        model.activePlayer().setLabelTo(position);
    }

    private void handleLabelTypeChange(LabelType labelType) {
        model.setActiveLabelType(labelType);
    }

    private void handlePassRequest() {
        model.passTurn();
    }

    private void setInteractionEnabled(boolean enabled) {
        fieldPanel.setInteractionEnabled(enabled);
        infoPanel.setInteractionEnabled(enabled);
    }

    private class PlayerObserver implements PlayerActionListener {
        @Override
        public void labelisPlaced(PlayerActionEvent event) {
            drawLabelOnField(event.label());
            setInteractionEnabled(false);
        }

        @Override
        public void labelIsReceived(PlayerActionEvent event) {
            drawLabelOnInfoPanel(event.label());
            setInteractionEnabled(true);
        }
    }

    private class GameObserver implements GameListener {
        @Override
        public void gameFinished(GameEvent event) {
            Player winner = event.player();
            if (winner != null) {
                String message = "Победил игрок '" + winner.name() + "' !!!";
                JOptionPane.showMessageDialog(null, message, "Победа!", JOptionPane.INFORMATION_MESSAGE);
                setInteractionEnabled(false);
            }
        }

        @Override
        public void playerExchanged(GameEvent event) {
            drawPlayerOnInfoPanel(event.player());
        }
    }

    private void drawPlayerOnInfoPanel(Player player) {
        infoPanel.showPlayer(player);
    }

    private void drawLabelOnInfoPanel(Label label) {
        infoPanel.showLabel(label);
    }

    private void drawLabelOnField(Label label) {
        fieldPanel.drawLabel(label);
    }
}
