package xzero.model.turn;

import java.util.ArrayList;
import java.util.List;

import xzero.model.Player;
import xzero.model.labels.LabelType;

/**
 * Управляет очередностью ходов игроков, типами используемых меток и лимитами пасов
 */
public class TurnManager {

    private final List<PlayerState> states = new ArrayList<>();
    private int activeIndex;
    private final int passLimitPerPlayer;

    /**
     * Создаёт менеджер ходов для заданного списка игроков
     *
     * @param players список игроков, участвующих в игре
     * @param passLimitPerPlayer максимальное количество пасов для каждого игрока
     *
     * @throws IllegalArgumentException если игроков нет
     */
    public TurnManager(List<Player> players, int passLimitPerPlayer) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("Список игроков не может быть пустым");
        }
        if (passLimitPerPlayer < 0) {
            throw new IllegalArgumentException("Лимит пасов не может быть отрицательным");
        }
        this.passLimitPerPlayer = passLimitPerPlayer;
        for (Player player : players) {
            states.add(new PlayerState(player, passLimitPerPlayer));
        }
        activeIndex = states.size() - 1;
    }

    /**
     * Возвращает игрока, чей ход сейчас активен
     *
     * @return активный игрок
     */
    public Player activePlayer() {
        return states.get(activeIndex).player();
    }

    /**
     * Возвращает тип метки активного игрока
     *
     * @return тип метки активного игрока
     */
    public LabelType activeLabelType() {
        return states.get(activeIndex).labelType();
    }

    /**
     * Сбрасывает состояние менеджера для начала новой игры
     */
    public void resetForNewGame() {
        for (PlayerState state : states) {
            state.reset(passLimitPerPlayer);
        }
        activeIndex = states.size() - 1;
        advanceToNextPlayer();
    }

    /**
     * Переключает ход на следующего игрока
     */
    public void advanceToNextPlayer() {
        activeIndex++;
        if (activeIndex >= states.size()) {
            activeIndex = 0;
        }
    }

    /**
     * Уменьшает количество оставшихся пасов у активного игрока
     */
    public void consumePassOfActive() {
        PlayerState state = states.get(activeIndex);
        state.consumePass();
    }

    /**
     * Возвращает количество оставшихся пасов для указанного игрока
     *
     * @param player игрок, для которого запрашивается информация
     * @return количество оставшихся пасов или 0, если игрок не найден
     */
    public int passesLeftFor(Player player) {
        PlayerState state = stateFor(player);
        return state == null ? 0 : state.passesLeft();
    }

    /**
     * Устанавливает тип метки для активного игрока
     *
     * @param labelType тип метки
     */
    public void setActiveLabelType(LabelType labelType) {
        states.get(activeIndex).setLabelType(labelType);
    }

    /**
     * Возвращает тип метки, выбранный указанным игроком
     *
     * @param player игрок, для которого запрашивается тип метки
     * @return тип метки или null, если игрок не найден
     */
    public LabelType labelTypeFor(Player player) {
        PlayerState state = stateFor(player);
        return state == null ? null : state.labelType();
    }

    /**
     * Возвращает внутреннее состояние для указанного игрока
     *
     * @param player игрок, состояние которого требуется получить
     * @return состояние игрока или null, если игрок не найден
     */
    private PlayerState stateFor(Player player) {
        for (PlayerState state : states) {
            if (state.player().equals(player)) {
                return state;
            }
        }
        return null;
    }

    /**
     * Внутреннее состояние игрока, хранящее выбранный тип метки и лимит пасов
     */
    private static final class PlayerState {
        private final Player player;
        private LabelType labelType;
        private int passesLeft;

        /**
         * Создаёт состояние игрока с заданным лимитом пасов
         *
         * @param player игрок
         * @param passLimitPerPlayer лимит пасов
         *
         * @throws IllegalArgumentException
         */
        PlayerState(Player player, int passLimitPerPlayer) {
            if (player == null) {
                throw new IllegalArgumentException("Игрок не может быть null");
            }
            this.player = player;
            reset(passLimitPerPlayer);
        }

        /**
         * Возвращает игрока, к которому относится состояние
         *
         * @return игрок
         */
        Player player() {
            return player;
        }

        /**
         * Возвращает выбранный тип метки
         *
         * @return тип метки
         */
        LabelType labelType() {
            return labelType;
        }

        /**
         * Возвращает количество оставшихся пасов
         *
         * @return количество пасов
         */
        int passesLeft() {
            return passesLeft;
        }

        /**
         * Сбрасывает состояние игрока к начальному
         *
         * @param passLimitPerPlayer лимит пасов
         */
        void reset(int passLimitPerPlayer) {
            this.labelType = LabelType.NORMAL;
            this.passesLeft = passLimitPerPlayer;
        }

        /**
         * Устанавливает тип метки для игрока
         *
         * @param labelType тип метки
         *
         * @throws IllegalArgumentException
         */
        void setLabelType(LabelType labelType) {
            if (labelType == null) {
                throw new IllegalArgumentException("Тип метки не может быть null");
            }
            this.labelType = labelType;
        }

        /**
         * Использует один пас игрока
         *
         * @throws IllegalStateException если пасов больше не осталось
         */
        void consumePass() {
            if (passesLeft <= 0) {
                throw new IllegalStateException("Лимит передач хода исчерпан");
            }
            passesLeft--;
        }
    }
}

