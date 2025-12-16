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
* Центральная модель игры, управляющая полем, игроками, ходами и завершением партии
*/
public class GameModel {
    // -------------------------------- Поле -------------------------------------
    private final GameField _field;
    private final FieldInitializer _fieldInitializer;

    /**
     * Возвращает игровое поле текущей игры
     *
     * @return игровое поле
     */
    public GameField field(){
        return _field;
    }

    // -------------------------------- Игроки -----------------------------------
    private final List<Player> _playerList = new ArrayList<>();
    private final TurnManager _turnManager;

    private LabelType _activeLabelType = LabelType.NORMAL;
    private boolean _secretModeEnabled = false;

    /**
     * Возвращает игрока, чей ход сейчас активен
     *
     * @return активный игрок
     */
    public Player activePlayer(){
        return _turnManager.activePlayer();
    }

    /**
     * Создаёт модель игры с настройками и зависимостями по умолчанию
     */
    public GameModel() {
        this(new GameField(), new CellFactory(), new LabelFactory(),
                new GridFieldInitializer(5, 5));
    }

    /**
     * Создаёт модель игры с переданными зависимостями и стратегией инициализации поля
     *
     * @param field игровое поле
     * @param cellFactory фабрика для создания ячеек
     * @param labelFactory фабрика для создания меток
     * @param fieldInitializer стратегия подготовки поля
     *
     * @throws IllegalArgumentException если любая из зависимостей равна null
     */
    public GameModel(GameField field, CellFactory cellFactory, LabelFactory labelFactory,
                     FieldInitializer fieldInitializer) {
        if (field == null || cellFactory == null || labelFactory == null || fieldInitializer == null) {
            throw new IllegalArgumentException("Все зависимости GameModel должны быть заданы");
        }
        this._field = field;
        this._cellFactory = cellFactory;
        this._labelFactory = labelFactory;
        this._fieldInitializer = fieldInitializer;

        PlayerObserver observer = new PlayerObserver();

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
    /**
     * Инициализирует игровое поле, создавая ячейки через фабрику
     */
    private void generateField() {
        _fieldInitializer.prepare(field(), _cellFactory);
    }

    // ----------------------------- Игровой процесс ----------------------------
    /**
     * Запускает новую игру, подготавливая поле и назначая активного игрока и метку
     */
    public void start() {
        generateField();

        _turnManager.resetForNewGame();
        _activeLabelType = _turnManager.activeLabelType();
        refreshActiveLabel();
        firePlayerExchanged(activePlayer());
    }

    /**
     * Переключает ход на следующего игрока и обновляет активную метку
     */
    private void exchangePlayer() {
        _turnManager.advanceToNextPlayer();

        _activeLabelType = _turnManager.activeLabelType();
        refreshActiveLabel();

        firePlayerExchanged(activePlayer());
    }

    /**
     * Передаёт ход следующему игроку, расходуя пас активного игрока
     *
     * @throws IllegalStateException если у активного игрока отсутствует активная метка
     */
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

    /**
     * Устанавливает выбранный тип метки для активного игрока
     *
     * @param labelType выбранный тип метки
     *
     * @throws IllegalArgumentException если тип метки равен null
     */
    public void setActiveLabelType(LabelType labelType) {
        if (labelType == null) {
            throw new IllegalArgumentException("Нельзя выбрать пустой тип метки");
        }
        _turnManager.setActiveLabelType(labelType);
        _activeLabelType = labelType;
        refreshActiveLabel();
    }

    /**
     * Включает или выключает режим секретности.
     *
     * @param enabled true ¢?" режим секретности включён, false ¢?" выключен
     */
    public void setSecretModeEnabled(boolean enabled) {
        _secretModeEnabled = enabled;
        refreshActiveLabel();
    }

    /**
     * Признак активного режима секретности.
     *
     * @return true, если секретность включена
     */
    public boolean secretModeEnabled() {
        return _secretModeEnabled;
    }

    /**
     * Возвращает текущий выбранный тип метки активного игрока
     *
     * @return тип активной метки
     */
    public LabelType activeLabelType() {
        return _activeLabelType;
    }

    /**
     * Пересоздаёт и назначает активную метку текущему активному игроку
     */
    private void refreshActiveLabel() {
        Player opponent = opponentFor(activePlayer());
        Label newLabel = _secretModeEnabled
                ? _labelFactory.createSecretLabel(activePlayer(), opponent, _activeLabelType)
                : _labelFactory.createLabel(activePlayer(), opponent, _activeLabelType);
        activePlayer().setActiveLabel(newLabel);
    }

    /**
     * Возвращает противника для указанного игрока
     *
     * @param player игрок, для которого ищется противник
     * @return противник игрока
     */
    private Player opponentFor(Player player) {
        for (Player p : _playerList) {
            if (!p.equals(player)) {
                return p;
            }
        }
        return player;
    }

    /**
     * Определяет победителя по наличию линии меток заданной длины
     *
     * @return победивший игрок или null, если победителя пока нет
     */
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
     * Возвращает количество доступных пасов для указанного игрока
     *
     * @param player игрок, для которого запрашивается количество пасов
     * @return количество оставшихся пасов
     */
    public int passesLeftFor(Player player) {
        return _turnManager.passesLeftFor(player);
    }

    // ------------------------- Реагируем на действия игрока ------------------
    /**
     * Обрабатывает событие размещения метки игроком и проверяет завершение игры
     *
     * @param event событие действия игрока
     */
    void handleLabelPlaced(PlayerActionEvent event) {
        if(event.player() == activePlayer()) {
            fireLabelIsPlaced(event);
        }

        Player winner = determineWinner();

        if(winner == null) {
            exchangePlayer();
        }
        else {
            fireGameFinished(winner);
        }
    }

    /**
     * Обрабатывает событие получения метки активным игроком
     *
     * @param event событие действия игрока
     */
    void handleLabelReceived(PlayerActionEvent event) {
        if(event.player() == activePlayer()) {
            fireLabelIsReceived(event);
        }
    }

    // ------------------------ Порождает события игры ----------------------------
    private final List<GameListener> _listenerList = new ArrayList<>();

    /**
     * Регистрирует слушателя событий игры
     *
     * @param listener слушатель событий игры
     */
    public void addGameListener(GameListener listener) {
        _listenerList.add(listener);
    }

    /**
     * Удаляет слушателя событий игры
     *
     * @param listener слушатель событий игры
     */
    public void removeGameListener(GameListener listener) {
        _listenerList.remove(listener);
    }

    /**
     * Генерирует событие завершения игры с указанным победителем
     *
     * @param winner победивший игрок
     */
    protected void fireGameFinished(Player winner) {
        GameEvent event = new GameEvent(this);
        event.setPlayer(winner);
        for (GameListener listener : _listenerList)
        {
            listener.gameFinished(event);
        }
    }

    /**
     * Генерирует событие смены активного игрока
     *
     * @param player новый активный игрок
     */
    protected void firePlayerExchanged(Player player) {
        GameEvent event = new GameEvent(this);
        event.setPlayer(player);
        for (GameListener listener : _listenerList) {
            listener.playerExchanged(event);
        }
    }

    // --------------------- Порождает события, связанные с игроками -------------
    private final List<PlayerActionListener> _playerListenerList = new ArrayList<>();

    /**
     * Регистрирует слушателя действий игрока
     *
     * @param listener слушатель действий игрока
     */
    public void addPlayerActionListener(PlayerActionListener listener) {
        _playerListenerList.add(listener);
    }

    /**
     * Удаляет слушателя действий игрока
     *
     * @param listener слушатель действий игрока
     */
    public void removePlayerActionListener(PlayerActionListener listener) {
        _playerListenerList.remove(listener);
    }

    /**
     * Генерирует событие установки метки игроком
     *
     * @param event событие действия игрока
     */
    protected void fireLabelIsPlaced(PlayerActionEvent event) {
        for (PlayerActionListener listener : _playerListenerList) {
            listener.labelIsPlaced(event);
        }
    }

    /**
     * Генерирует событие получения метки игроком
     *
     * @param event событие действия игрока
     */
    protected void fireLabelIsReceived(PlayerActionEvent event) {
        for (PlayerActionListener listener : _playerListenerList) {
            listener.labelIsReceived(event);
        }
    }

    /**
     * Внутренний слушатель действий игрока, перенаправляющий события в GameModel
     */
    private final class PlayerObserver implements PlayerActionListener {

        /**
         * Обрабатывает событие установки метки игроком и передаёт его модели игры
         *
         * @param event событие действия игрока
         */
        @Override
        public void labelIsPlaced(PlayerActionEvent event) {
            handleLabelPlaced(event);
        }

        /**
         * Обрабатывает событие получения метки игроком и передаёт его модели игры
         *
         * @param event событие действия игрока
         */
        @Override
        public void labelIsReceived(PlayerActionEvent event) {
            handleLabelReceived(event);
        }
    }
}
