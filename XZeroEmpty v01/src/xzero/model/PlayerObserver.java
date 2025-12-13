package xzero.model;

import xzero.model.events.PlayerActionEvent;
import xzero.model.events.PlayerActionListener;

/**
 * Наблюдатель за действиями игроков, делегирует обработку модели игры.
 */
public class PlayerObserver implements PlayerActionListener {
    private final GameModel gameModel;

    public PlayerObserver(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    @Override
    public void labelisPlaced(PlayerActionEvent e) {
        gameModel.handleLabelPlaced(e);
    }

    @Override
    public void labelIsReceived(PlayerActionEvent e) {
        gameModel.handleLabelReceived(e);
    }
}
