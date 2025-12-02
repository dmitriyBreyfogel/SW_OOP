package xzero.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import xzero.model.events.PlayerActionEvent;
import xzero.model.events.PlayerActionListener;
import xzero.model.labels.Label;

/**
 *  Игрок, который размещает предложенную ему метку
 */
public class Player {

    // Имя игрока
    private String _name;

    public void setName(String name) {
        _name = name;
    }

    public String name() {
        return _name;
    }

    // Связь с полем
    private GameField _field;

    public Player (GameField field, String name) {
        _field = field;
        _name = name;
    }

    // Метка, которую нужно установить
    private Label _label;

    public void setActiveLabel(Label label) {
        if (label == null) {
            throw new IllegalArgumentException("Player: метка не может быть null");
        }
        _label = label;
        _label.setPlacedBy(this);

        fireLabelIsReceived(_label);
    }

    public Label takeActiveLabel() {
        if (_label == null) {
            throw new IllegalStateException("Player: метка не может быть null");
        }
        Label tmp = _label;
        _label = null;
        return tmp;
    }

    public Label activeLabel() {
        return _label;
    }

    public void setLabelTo(Point pos){
        if (_label == null) {
            throw new IllegalStateException("Player: метка не может быть null");
        }
        _field.setLabel(pos, _label);

        fireLabelIsPlaced(_label);

        _label = null;
    }

    private ArrayList<Label> _labels = new ArrayList<>();

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

    public void addPlayerActionListener(PlayerActionListener listener) {
        if (listener != null) {
            _listenerList.add(listener);
        }
    }

    public void removePlayerActionListener(PlayerActionListener listener) {
        _listenerList.remove(listener);
    }

    protected void fireLabelIsPlaced(Label label) {
        PlayerActionEvent event = new PlayerActionEvent(this);
        event.setPlayer(this);
        event.setLabel(label);
        for (PlayerActionListener listener : _listenerList) {
            listener.labelisPlaced(event);
        }
    }

    protected void fireLabelIsReceived(Label label) {
        PlayerActionEvent event = new PlayerActionEvent(this);
        event.setPlayer(this);
        event.setLabel(label);
        for (PlayerActionListener listener : _listenerList) {
            listener.labelIsReceived(event);
        }
    }
}
