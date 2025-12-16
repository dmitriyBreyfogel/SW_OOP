package xzero.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import xzero.model.events.PlayerActionEvent;
import xzero.model.events.PlayerActionListener;
import xzero.model.labels.Label;
import xzero.model.labels.SecretLabel;

/**
 *  Игрок, который получает активную метку и размещает её на игровом поле
 */
public class Player {

    private String _name;

    /**
     * Устанавливает имя игрока
     *
     * @param name имя игрока
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * Возвращает имя игрока
     *
     * @return имя игрока
     */
    public String name() {
        return _name;
    }

    private GameField _field;

    /**
     * Создаёт игрока, связанного с указанным полем и именем
     *
     * @param field игровое поле, на котором действует игрок
     * @param name имя игрока
     */
    public Player (GameField field, String name) {
        _field = field;
        _name = name;
    }

    private Label _label;

    /**
     * Назначает игроку активную метку, которую он должен установить
     *
     * @param label метка, назначаемая игроку
     *
     * @throws IllegalArgumentException если метка равна null
     */
    public void setActiveLabel(Label label) {
        if (label == null) {
            throw new IllegalArgumentException("Player: метка не может быть null");
        }
        _label = label;
        _label.setPlacedBy(this);

        fireLabelIsReceived(_label);
    }

    /**
     * Забирает у игрока текущую активную метку
     *
     * @return активная метка
     *
     * @throws IllegalStateException если активная метка отсутствует
     */
    public Label takeActiveLabel() {
        if (_label == null) {
            throw new IllegalStateException("Player: метка не может быть null");
        }
        Label tmp = _label;
        _label = null;
        return tmp;
    }

    /**
     * Возвращает текущую активную метку игрока
     *
     * @return активная метка или null, если метка не назначена
     */
    public Label activeLabel() {
        return _label;
    }

    /**
     * Устанавливает активную метку игрока в указанную позицию поля
     *
     * @param pos позиция на поле, куда требуется поставить метку
     *
     * @throws IllegalStateException если активная метка отсутствует
     */
    public void setLabelTo(Point pos){
        if (_label == null) {
            throw new IllegalStateException("Player: метка не может быть null");
        }
        Label toPlace = _label;
        if (_label instanceof SecretLabel) {
            toPlace = ((SecretLabel)_label).reveal();
            toPlace.setPlacedBy(this);
        }
        _field.setLabel(pos, toPlace);

        fireLabelIsPlaced(toPlace);

        _label = null;
    }

    private ArrayList<Label> _labels = new ArrayList<>();

    /**
     * Возвращает список всех меток, принадлежащих данному игроку на поле
     *
     * @return неизменяемый список меток игрока
     */
    public List<Label> labels(){
        _labels.clear();
        for(Label obj: _field.labels()) {
            if(obj.owner().equals(this)) {
                _labels.add(obj);
            }
        }

        return Collections.unmodifiableList(_labels);
    }

    private final ArrayList<PlayerActionListener> _listenerList = new ArrayList<>();

    /**
     * Регистрирует слушателя действий игрока
     *
     * @param listener слушатель действий игрока
     */
    public void addPlayerActionListener(PlayerActionListener listener) {
        if (listener != null) {
            _listenerList.add(listener);
        }
    }

    /**
     * Удаляет слушателя действий игрока
     *
     * @param listener слушатель действий игрока
     */
    public void removePlayerActionListener(PlayerActionListener listener) {
        _listenerList.remove(listener);
    }

    /**
     * Генерирует событие установки метки игроком
     *
     * @param label метка, которая была установлена
     */
    protected void fireLabelIsPlaced(Label label) {
        PlayerActionEvent event = new PlayerActionEvent(this);
        event.setPlayer(this);
        event.setLabel(label);
        for (PlayerActionListener listener : _listenerList) {
            listener.labelIsPlaced(event);
        }
    }

    /**
     * Генерирует событие получения активной метки игроком
     *
     * @param label метка, которая была получена
     */
    protected void fireLabelIsReceived(Label label) {
        PlayerActionEvent event = new PlayerActionEvent(this);
        event.setPlayer(this);
        event.setLabel(label);
        for (PlayerActionListener listener : _listenerList) {
            listener.labelIsReceived(event);
        }
    }
}
