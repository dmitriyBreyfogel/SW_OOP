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
import xzero.model.navigation.Direction;
import xzero.model.setup.FieldInitializer;
import xzero.model.setup.GridFieldInitializer;
import xzero.model.turn.TurnManager;

/**
/* Aбстракция всей игры; генерирует стартовую обстановку; поочередно передает 
* ход игрокам, задавая им метку для установки на поле; следит за игроками с 
* целью определения конца игры
*/
public class GameModel {
    // -------------------------------- Поле -------------------------------------
    private final GameField _field;
    private final FieldInitializer _fieldInitializer;

    public GameField field(){
        return _field;
    }

    // -------------------------------- Игроки -----------------------------------

    private final List<Player> _playerList = new ArrayList<>();
    private final TurnManager _turnManager;

    private LabelType _activeLabelType = LabelType.NORMAL;

    public Player activePlayer(){
        return _turnManager.activePlayer();
    }

    public GameModel() {
        this(new GameField(), new CellFactory(), new LabelFactory(),
                new GridFieldInitializer(5, 5));
    }

    public GameModel(GameField field, CellFactory cellFactory, LabelFactory labelFactory,
                     FieldInitializer fieldInitializer) {
        if (field == null || cellFactory == null || labelFactory == null || fieldInitializer == null) {
            throw new IllegalArgumentException("Все зависимости GameModel должны быть заданы");
        }
        this._field = field;
        this._cellFactory = cellFactory;
        this._labelFactory = labelFactory;
        this._fieldInitializer = fieldInitializer;

        PlayerObserver observer = new PlayerObserver(this);

        Player xPlayer = new Player(field(), "X");
        xPlayer.addPlayerActionListener(observer);
        _playerList.add(xPlayer);

        Player oPlayer = new Player(field(), "O");
        oPlayer.addPlayerActionListener(observer);
        _playerList.add(oPlayer);

        _turnManager = new TurnManager(_playerList, PASS_LIMIT_PER_PLAYER);
    }

    private static final int PASS_LIMIT_PER_PLAYER = 1;
    private static final int WINNER_LINE_LENGTH = 5;

    private final CellFactory _cellFactory;
    private final LabelFactory _labelFactory;

    // ---------------------- Порождение обстановки на поле ---------------------
    private void generateField() {
        _fieldInitializer.prepare(field(), _cellFactory);
    }

    // ----------------------------- Игровой процесс ----------------------------
    public void start() {
        generateField();

        _turnManager.resetForNewGame();
        _activeLabelType = _turnManager.activeLabelType();
        refreshActiveLabel();
        firePlayerExchanged(activePlayer());
    }

    private void exchangePlayer() {
        _turnManager.advanceToNextPlayer();

        _activeLabelType = _turnManager.activeLabelType();
        refreshActiveLabel();

        firePlayerExchanged(activePlayer());
    }

    // Передать ход противнику: активная метка переходит новому активному игроку
    public void passTurn() {
        if (activePlayer().activeLabel() == null) {
            throw new IllegalStateException("Нельзя передать ход: у активного игрока нет активной метки");
        }

        _turnManager.consumePassOfActive();

        activePlayer().takeActiveLabel();

        _turnManager.advanceToNextPlayer();

        _activeLabelType = _turnManager.activeLabelType();
        refreshActiveLabel();

        firePlayerExchanged(activePlayer());
    }

    public void setActiveLabelType(LabelType labelType) {
        if (labelType == null) {
            throw new IllegalArgumentException("Нельзя выбрать пустой тип метки");
        }
        _turnManager.setActiveLabelType(labelType);
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

    /**
     * Возвращает количество доступных пасов для указанного игрока.
     */
    public int passesLeftFor(Player player) {
        return _turnManager.passesLeftFor(player);
    }

    // ------------------------- Реагируем на действия игрока ------------------
    void handleLabelPlaced(PlayerActionEvent e) {
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

    void handleLabelReceived(PlayerActionEvent e) {
        if(e.player() == activePlayer()) {
            fireLabelIsRecived(e);
        }
    }

    // ------------------------ Порождает события игры ----------------------------
    private final List<GameListener> _listenerList = new ArrayList<>();

    public void addGameListener(GameListener l) {
        _listenerList.add(l);
    }

    public void removeGameListener(GameListener l) {
        _listenerList.remove(l);
    }

    protected void fireGameFinished(Player winner) {
        GameEvent event = new GameEvent(this);
        event.setPlayer(winner);
        for (GameListener listener : _listenerList)
        {
            listener.gameFinished(event);
        }
    }

    protected void firePlayerExchanged(Player p) {
        GameEvent event = new GameEvent(this);
        event.setPlayer(p);
        for (GameListener listener : _listenerList) {
            listener.playerExchanged(event);
        }
    }

    // --------------------- Порождает события, связанные с игроками -------------
    private final List<PlayerActionListener> _playerListenerList = new ArrayList<>();

    public void addPlayerActionListener(PlayerActionListener l) {
        _playerListenerList.add(l);
    }

    public void removePlayerActionListener(PlayerActionListener l) {
        _playerListenerList.remove(l);
    }

    protected void fireLabelIsPlaced(PlayerActionEvent e) {
        for (PlayerActionListener listener : _playerListenerList) {
            listener.labelIsPlaced(e);
        }
    }

    protected void fireLabelIsRecived(PlayerActionEvent e) {
        for (PlayerActionListener listener : _playerListenerList) {
            listener.labelIsReceived(e);
        }
    }
}
