package xzero.model.events;

import java.util.EventListener;

/**
 * Слушатель событий, связанных с изменением состояния игры
 */
public interface GameListener extends EventListener {

    /**
     * Вызывается при завершении игры
     *
     * @param event событие завершения игры
     */
    void gameFinished(GameEvent event);

    /**
     * Вызывается при смене активного игрока
     *
     * @param event событие смены игрока
     */
    void playerExchanged(GameEvent event);
}
