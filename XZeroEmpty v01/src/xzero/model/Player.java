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

    // --------------------------------- Имя игрока -------------------------------
    private String _name;

    public void setName(String name) {
        _name = name;
    }

    public String name() {
        return _name;
    }

    // ----------------------- Устанавливаем связь с полем -----------------------
    GameField _field;

    public Player (GameField field, String name) {
        _field = field;
        _name = name;
    }

    // ---------------------- Метка, которую нужно установить ---------------------
    xzero.model.labels.Label _label;

    public void setActiveLabel(xzero.model.labels.Label l) {
        if (l == null) {
            throw new IllegalArgumentException("Игроку выдана null-метка");
        }
        _label = l;
        _label.setPlayer(this);

        fireLabelIsReceived(_label);
    }

    public xzero.model.labels.Label takeActiveLabel() {
        if (_label == null) {
            throw new IllegalStateException("Активной метки нет — передавать нечего");
        }
        xzero.model.labels.Label tmp = _label;
        _label = null;
        return tmp;
    }

    public xzero.model.labels.Label activeLabel() {
        return _label;
    }

    public void setLabelTo(Point pos){
        if (_label == null) {
            throw new IllegalStateException("Активная метка не задана");
        }
        _field.setLabel(pos, _label);

        fireLabelIsPlaced(_label);

        _label = null;
    }

    private ArrayList<xzero.model.labels.Label> _labels = new ArrayList<>();

    public List<xzero.model.labels.Label> labels(){
        _labels.clear();
        for(xzero.model.labels.Label obj: _field.labels())
        {
            if(obj.player().equals(this))
            { _labels.add(obj); }
        }

        return Collections.unmodifiableList(_labels);
    }

    // ---------------------- Порождает события -----------------------------
    private final ArrayList<PlayerActionListener> _listenerList = new ArrayList<>();

    public void addPlayerActionListener(PlayerActionListener l) {
        if (l != null) _listenerList.add(l);
    }

    public void removePlayerActionListener(PlayerActionListener l) {
        _listenerList.remove(l);
    }

    protected void fireLabelIsPlaced(xzero.model.labels.Label l) {
        PlayerActionEvent e = new PlayerActionEvent(this);
        e.setPlayer(this);
        e.setLabel(l);
        for (PlayerActionListener listener : _listenerList) {
            listener.labelisPlaced(e);
        }
    }

    protected void fireLabelIsReceived(Label l) {
        PlayerActionEvent e = new PlayerActionEvent(this);
        e.setPlayer(this);
        e.setLabel(l);
        for (PlayerActionListener listener : _listenerList) {
            listener.labelIsReceived(e);
        }
    }
}
