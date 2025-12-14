package xzero.model.events;

import java.util.EventObject;
import xzero.model.Player;
import xzero.model.labels.Label;

/**
 * Событие, описывающее действие игрока и связанную с ним метку
 */
public class PlayerActionEvent extends EventObject {
    // -------------------------------- Игрок --------------------------------------
    Player _player;

    /**
     * Устанавливает игрока, выполнившего действие
     *
     * @param player игрок, связанный с событием
     */
    public void setPlayer(Player player){
        _player = player;
    }

    /**
     * Возвращает игрока, связанного с данным событием
     *
     * @return игрок события
     */
    public Player player(){
        return _player;
    }

    // ------------------------- Активная метка ------------------------------
    Label _label;

    /**
     * Устанавливает метку, связанную с действием игрока
     *
     * @param label метка, связанная с событием
     */
    public void setLabel(Label label){
        _label = label;
    }

    /**
     * Возвращает метку, связанную с данным событием
     *
     * @return метка события
     */
    public Label label(){
        return _label;
    }

    /**
     * Создаёт событие действия игрока с указанным источником
     *
     * @param source источник события
     */
    public PlayerActionEvent(Object source) { 
        super(source); 
    } 
} 
