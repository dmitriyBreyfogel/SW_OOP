package xzero.model.events;

import java.util.EventObject;
import xzero.model.Player;

/**
 * Событие, описывающее изменение состояния игры и связанного с ним игрока
 */
public class GameEvent extends EventObject {
    
    Player _player;

    /**
     * Устанавливает игрока, связанного с данным игровым событием
     *
     * @param player игрок, участвующий в событии
     */
    public void setPlayer(Player player) {
        _player = player;
    }

    /**
     * Возвращает игрока, связанного с данным игровым событием
     *
     * @return игрок события
     */
    public Player player(){
        return _player;
    }

    /**
     * Создаёт игровое событие с указанным источником
     *
     * @param source источник события
     */
    public GameEvent(Object source) { 
        super(source); 
    } 
} 
