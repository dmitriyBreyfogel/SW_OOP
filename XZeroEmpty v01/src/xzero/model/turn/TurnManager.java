package xzero.model.turn;

import java.util.ArrayList;
import java.util.List;

import xzero.model.Player;
import xzero.model.labels.LabelType;

/**
 * Управляет очередностью ходов, лимитами пасов и выбранными типами меток для игроков.
 */
public class TurnManager {

    private final List<PlayerState> states = new ArrayList<>();
    private int activeIndex;
    private final int passLimitPerPlayer;

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

    public Player activePlayer() {
        return states.get(activeIndex).player();
    }

    public LabelType activeLabelType() {
        return states.get(activeIndex).labelType();
    }

    public void resetForNewGame() {
        for (PlayerState state : states) {
            state.reset(passLimitPerPlayer);
        }
        activeIndex = states.size() - 1;
        advanceToNextPlayer();
    }

    public void advanceToNextPlayer() {
        activeIndex++;
        if (activeIndex >= states.size()) {
            activeIndex = 0;
        }
    }

    public void consumePassOfActive() {
        PlayerState state = states.get(activeIndex);
        state.consumePass();
    }

    public int passesLeftFor(Player player) {
        PlayerState state = stateFor(player);
        return state == null ? 0 : state.passesLeft();
    }

    public void setActiveLabelType(LabelType labelType) {
        states.get(activeIndex).setLabelType(labelType);
    }

    public LabelType labelTypeFor(Player player) {
        PlayerState state = stateFor(player);
        return state == null ? null : state.labelType();
    }

    private PlayerState stateFor(Player player) {
        for (PlayerState state : states) {
            if (state.player().equals(player)) {
                return state;
            }
        }
        return null;
    }

    private static final class PlayerState {
        private final Player player;
        private LabelType labelType;
        private int passesLeft;

        PlayerState(Player player, int passLimitPerPlayer) {
            if (player == null) {
                throw new IllegalArgumentException("Игрок не может быть null");
            }
            this.player = player;
            reset(passLimitPerPlayer);
        }

        Player player() {
            return player;
        }

        LabelType labelType() {
            return labelType;
        }

        int passesLeft() {
            return passesLeft;
        }

        void reset(int passLimitPerPlayer) {
            this.labelType = LabelType.NORMAL;
            this.passesLeft = passLimitPerPlayer;
        }

        void setLabelType(LabelType labelType) {
            if (labelType == null) {
                throw new IllegalArgumentException("Тип метки не может быть null");
            }
            this.labelType = labelType;
        }

        void consumePass() {
            if (passesLeft <= 0) {
                throw new IllegalStateException("Лимит передач хода исчерпан");
            }
            passesLeft--;
        }
    }
}

