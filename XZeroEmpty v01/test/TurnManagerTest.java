import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xzero.model.GameField;
import xzero.model.Player;
import xzero.model.labels.LabelType;
import xzero.model.turn.TurnManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TurnManager: очередность ходов, типы меток и лимиты пасов")
class TurnManagerTest {

    private List<Player> players() {
        GameField field = new GameField();
        List<Player> players = new ArrayList<>();
        players.add(new Player(field, "A"));
        players.add(new Player(field, "B"));
        return players;
    }

    @Test
    @DisplayName("Тест №1: конструктор запрещает пустой список игроков")
    void constructorRejectsEmptyPlayers() {
        assertThrows(IllegalArgumentException.class, () -> new TurnManager(new ArrayList<>(), 1));
    }

    @Test
    @DisplayName("Тест №2: конструктор запрещает отрицательный лимит пасов")
    void constructorRejectsNegativePassLimit() {
        assertThrows(IllegalArgumentException.class, () -> new TurnManager(players(), -1));
    }

    @Test
    @DisplayName("Тест №3: активным изначально становится последний игрок в списке")
    void activeStartsFromLastPlayer() {
        TurnManager manager = new TurnManager(players(), 1);
        assertEquals("B", manager.activePlayer().name());
    }

    @Test
    @DisplayName("Тест №4: переход хода циклически перебирает игроков")
    void advanceCyclesPlayers() {
        TurnManager manager = new TurnManager(players(), 1);
        manager.advanceToNextPlayer();
        assertEquals("A", manager.activePlayer().name());
        manager.advanceToNextPlayer();
        assertEquals("B", manager.activePlayer().name());
    }

    @Test
    @DisplayName("Тест №5: consumePassOfActive уменьшает лимит для активного игрока")
    void consumePassReducesLimit() {
        TurnManager manager = new TurnManager(players(), 2);
        manager.consumePassOfActive();
        assertEquals(1, manager.passesLeftFor(manager.activePlayer()));
    }

    @Test
    @DisplayName("Тест №6: попытка передать ход при исчерпанном лимите запрещена")
    void consumePassThrowsWhenExhausted() {
        TurnManager manager = new TurnManager(players(), 1);
        manager.consumePassOfActive();
        assertThrows(IllegalStateException.class, manager::consumePassOfActive);
    }

    @Test
    @DisplayName("Тест №7: resetForNewGame сбрасывает лимиты и возвращает к первому игроку")
    void resetRestoresLimitsAndOrder() {
        TurnManager manager = new TurnManager(players(), 1);
        manager.consumePassOfActive();
        manager.resetForNewGame();
        assertEquals("A", manager.activePlayer().name());
        assertEquals(1, manager.passesLeftFor(manager.activePlayer()));
    }

    @Test
    @DisplayName("Тест №8: setActiveLabelType запрещает null")
    void setActiveLabelTypeRejectsNull() {
        TurnManager manager = new TurnManager(players(), 1);
        assertThrows(IllegalArgumentException.class, () -> manager.setActiveLabelType(null));
    }

    @Test
    @DisplayName("Тест №9: выбранный тип метки сохраняется за конкретным игроком")
    void labelTypeStaysWithPlayer() {
        TurnManager manager = new TurnManager(players(), 1);
        manager.resetForNewGame();
        manager.setActiveLabelType(LabelType.HIDDEN);

        manager.advanceToNextPlayer();
        assertEquals(LabelType.NORMAL, manager.activeLabelType());

        manager.advanceToNextPlayer();
        assertEquals(LabelType.HIDDEN, manager.activeLabelType());
    }

    @Test
    @DisplayName("Тест №10: неизвестный игрок имеет 0 доступных пасов")
    void passesLeftForUnknownPlayerIsZero() {
        TurnManager manager = new TurnManager(players(), 2);
        Player stranger = new Player(new GameField(), "Ghost");
        assertEquals(0, manager.passesLeftFor(stranger));
    }

    @Test
    @DisplayName("Тест №11: labelTypeFor возвращает null для неизвестного игрока")
    void labelTypeForUnknownPlayerIsNull() {
        TurnManager manager = new TurnManager(players(), 2);
        Player stranger = new Player(new GameField(), "Ghost");
        assertNull(manager.labelTypeFor(stranger));
    }

    @Test
    @DisplayName("Тест №12: выбор типа метки для активного игрока обновляет activeLabelType")
    void activeLabelTypeReflectsSelection() {
        TurnManager manager = new TurnManager(players(), 1);
        manager.resetForNewGame();
        manager.setActiveLabelType(LabelType.HIDDEN);
        assertEquals(LabelType.HIDDEN, manager.activeLabelType());
    }

    @Test
    @DisplayName("Тест №13: уменьшение лимита касается только активного игрока")
    void passConsumptionIsPlayerSpecific() {
        TurnManager manager = new TurnManager(players(), 2);
        Player first = manager.activePlayer();
        manager.consumePassOfActive();
        manager.advanceToNextPlayer();
        assertEquals(2, manager.passesLeftFor(manager.activePlayer()));
        assertEquals(1, manager.passesLeftFor(first));
    }

    @Test
    @DisplayName("Тест №14: resetForNewGame возвращает типы меток к NORMAL для всех")
    void resetRestoresLabelTypes() {
        TurnManager manager = new TurnManager(players(), 1);
        manager.resetForNewGame();
        manager.setActiveLabelType(LabelType.HIDDEN);
        manager.advanceToNextPlayer();
        manager.setActiveLabelType(LabelType.HIDDEN);

        manager.resetForNewGame();

        assertEquals(LabelType.NORMAL, manager.activeLabelType());
        manager.advanceToNextPlayer();
        assertEquals(LabelType.NORMAL, manager.activeLabelType());
    }
}
