package xzero.model.events;

import java.util.EventListener;

/**
 * Слушатель событий, связанных с действиями игрока
 */
public interface PlayerActionListener extends EventListener {

    /**
     * Вызывается при установке метки игроком на игровое поле
     *
     * @param event событие действия игрока
     */
    void labelIsPlaced(PlayerActionEvent event);

    /**
     * Вызывается при получении игроком активной метки
     *
     * @param event событие действия игрока
     */
    void labelIsReceived(PlayerActionEvent event);
}
