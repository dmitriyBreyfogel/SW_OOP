package xzero.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import xzero.model.events.GameEvent;
import xzero.model.events.GameListener;
import xzero.model.events.PlayerActionEvent;
import xzero.model.events.PlayerActionListener;
import xzero.model.factory.CellFactory;
import xzero.model.factory.LabelFactory;
import xzero.model.labels.Label;
import xzero.model.labels.LabelType;
import xzero.model.labels.DelegatedLabel;
import xzero.model.labels.HiddenLabel;
import xzero.model.navigation.Direction;

/**
/* Aбстракция всей игры; генерирует стартовую обстановку; поочередно передает 
* ход игрокам, задавая им метку для установки на поле; следит за игроками с 
* целью определения конца игры
*/
public class GameModel {
    // -------------------------------- Поле -------------------------------------
    private final GameField _field = new GameField();

    private static final int PASS_LIMIT_PER_PLAYER = 1;

    private final ArrayList<Integer> _passesLeft = new ArrayList<>();

    public GameField field(){
        return _field;
    }

    // -------------------------------- Игроки -----------------------------------

    private ArrayList<Player> _playerList = new ArrayList<>();
    private int _activePlayer;

    private LabelType _activeLabelType = LabelType.NORMAL;

    public Player activePlayer(){
        return _playerList.get(_activePlayer);
    }

    public GameModel() {
        field().setSize(5, 5);

        Player p;
        PlayerObserver observer = new PlayerObserver();

        p = new Player(field(), "X");
        p.addPlayerActionListener(observer);
        _playerList.add(p);
        _passesLeft.add(PASS_LIMIT_PER_PLAYER);
        _activePlayer = 0;

        p = new Player(field(), "O");
        p.addPlayerActionListener(observer);
        _playerList.add(p);
        _passesLeft.add(PASS_LIMIT_PER_PLAYER);
    }

    // ---------------------- Порождение обстановки на поле ---------------------
    private CellFactory _cellFactory = new CellFactory();

    private void generateField() {

        field().clear();
        field().setSize(5, 5);
        for(int row = 1; row <= field().height(); row++) {
            for(int col = 1; col <= field().width(); col++) {
                field().setCell(new Point(col, row), _cellFactory.createCell());
            }
        }
    }

    // ----------------------------- Игровой процесс ----------------------------
    public void start() {
        generateField();

        resetPassCounters();

        _activePlayer = _playerList.size()-1;
        exchangePlayer();
    }

    private LabelFactory _labelFactory = new LabelFactory();

    private void exchangePlayer() {
        _activePlayer++;
        if(_activePlayer >= _playerList.size())     _activePlayer = 0;

        _activeLabelType = LabelType.NORMAL;
        refreshActiveLabel();

        firePlayerExchanged(activePlayer());
    }

    // Передать ход противнику: активная метка переходит новому активному игроку
    public void passTurn() {
        if (activePlayer().activeLabel() == null) {
            throw new IllegalStateException("Нельзя передать ход: у активного игрока нет активной метки");
        }
        int left = _passesLeft.get(_activePlayer);
        if (left <= 0) {
            throw new IllegalStateException("Лимит передач хода исчерпан");
        }

        Label l = activePlayer().takeActiveLabel();

        _passesLeft.set(_activePlayer, left - 1);

        _activePlayer++;
        if (_activePlayer >= _playerList.size()) _activePlayer = 0;

        _activeLabelType = detectLabelType(l);
        activePlayer().setActiveLabel(l);

        firePlayerExchanged(activePlayer());
    }

    public void setActiveLabelType(LabelType labelType) {
        if (labelType == null) {
            throw new IllegalArgumentException("Нельзя выбрать пустой тип метки");
        }
        _activeLabelType = labelType;
        refreshActiveLabel();
    }

    public LabelType activeLabelType() {
        return _activeLabelType;
    }

    private void refreshActiveLabel() {
        Player opponent = opponentFor(activePlayer());
        Label newLabel = _labelFactory.createLabel(activePlayer(), opponent, _activeLabelType);
        activePlayer().setActiveLabel(newLabel);
    }

    private Player opponentFor(Player player) {
        for (Player p : _playerList) {
            if (!p.equals(player)) {
                return p;
            }
        }
        return player;
    }

    private LabelType detectLabelType(Label label) {
        if (label instanceof HiddenLabel) {
            return LabelType.HIDDEN;
        }
        if (label instanceof DelegatedLabel) {
            return LabelType.DELEGATED;
        }
        return LabelType.NORMAL;
    }

    private static int WINNER_LINE_LENGTH = 5;

    private Player determineWinner(){
        for(int row = 1; row <= field().height(); row++) {
            for(int col = 1; col <= field().width(); col++) {
                Point pos = new Point(col, row);
                Direction direct = Direction.north();
                for(int  i = 1; i <= 8; i++) {
                    direct = direct.rightword();

                    List<Label> line = field().labelLine(pos, direct);

                    if(line.size() >= WINNER_LINE_LENGTH) {
                        return line.get(0).owner();
                    }
                }
            }
        }

        return null;
    }

    private void resetPassCounters() {
        for (int i = 0; i < _passesLeft.size(); i++) {
            _passesLeft.set(i, PASS_LIMIT_PER_PLAYER);
        }
    }

    // ------------------------- Реагируем на действия игрока ------------------
    private class PlayerObserver implements PlayerActionListener{
        @Override
        public void labelisPlaced(PlayerActionEvent e) {
            if(e.player() == activePlayer()) {
                fireLabelIsPlaced(e);
            }

            Player winner = determineWinner();

            if(winner == null) {
                exchangePlayer();
            }
            else {
                fireGameFinished(winner);
            }
        }

        @Override
        public void labelIsReceived(PlayerActionEvent e) {
            if(e.player() == activePlayer()) {
                fireLabelIsRecived(e);
            }
        }
    }

    // ------------------------ Порождает события игры ----------------------------
    private ArrayList _listenerList = new ArrayList();

    public void addGameListener(GameListener l) {
        _listenerList.add(l);
    }

    public void removeGameListener(GameListener l) {
        _listenerList.remove(l);
    }

    protected void fireGameFinished(Player winner) {
        GameEvent event = new GameEvent(this);
        event.setPlayer(winner);
        for (Object listner : _listenerList)
        {
            ((GameListener)listner).gameFinished(event);
        }
    }

    protected void firePlayerExchanged(Player p) {
        GameEvent event = new GameEvent(this);
        event.setPlayer(p);
        for (Object listner : _listenerList) {
            ((GameListener)listner).playerExchanged(event);
        }
    }

    // --------------------- Порождает события, связанные с игроками -------------
    private ArrayList _playerListenerList = new ArrayList();

    public void addPlayerActionListener(PlayerActionListener l) {
        _playerListenerList.add(l);
    }

    public void removePlayerActionListener(PlayerActionListener l) {
        _playerListenerList.remove(l);
    }

    protected void fireLabelIsPlaced(PlayerActionEvent e) {
        for (Object listner : _playerListenerList) {
            ((PlayerActionListener)listner).labelisPlaced(e);
        }
    }

    protected void fireLabelIsRecived(PlayerActionEvent e) {
        for (Object listner : _playerListenerList) {
            ((PlayerActionListener)listner).labelIsReceived(e);
        }
    }
}
