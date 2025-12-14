import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xzero.model.GameModel;
import xzero.model.Player;
import xzero.model.events.GameEvent;
import xzero.model.events.GameListener;
import xzero.model.events.PlayerActionEvent;
import xzero.model.events.PlayerActionListener;
import xzero.model.labels.Label;
import xzero.model.labels.LabelType;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GameModel: старт, очередность ходов, события, пас и победа")
class GameModelTest {

    private GameModel model;

    @BeforeEach
    void setup() {
        model = new GameModel();
        model.start();
    }

    @Test
    @DisplayName("Тест №1: после старта активный игрок получает метку нужного типа")
    void activePlayerGetsLabelOnStart() {
        Label label = model.activePlayer().activeLabel();
        assertNotNull(label);
        assertEquals(model.activePlayer(), label.owner());
        assertEquals(model.activePlayer().name(), label.symbol());
    }

    @Test
    @DisplayName("Тест №2: событие playerExchanged приходит при старте")
    void playerExchangedOnStart() {
        AtomicReference<Player> last = new AtomicReference<>();
        model.addGameListener(new GameListener() {
            public void gameFinished(GameEvent e) {}
            public void playerExchanged(GameEvent e) { last.set(e.player()); }
        });
        model.start();
        assertNotNull(last.get());
    }

    @Test
    @DisplayName("Тест №3: после установки метки ход переходит другому игроку")
    void turnChangesAfterPlacement() {
        Player first = model.activePlayer();
        Point p = new Point(1, 1);
        model.activePlayer().setLabelTo(p);
        Player second = model.activePlayer();
        assertNotSame(first, second);
        assertNotNull(model.activePlayer().activeLabel());
    }

    @Test
    @DisplayName("Тест №4: пас отдаёт ход противнику с его типом метки")
    void passGivesOpponentLabelWithOwnType() {
        // Передаём ход первому игроку сопернику и настраиваем его тип метки
        model.activePlayer().setLabelTo(new Point(1, 1)); // переходим ко второму игроку
        model.setActiveLabelType(LabelType.HIDDEN);
        Label opponentLabel = model.activePlayer().activeLabel();

        model.passTurn(); // ход возвращается первому игроку

        assertEquals("?", opponentLabel.symbol());
        assertEquals("X", model.activePlayer().activeLabel().symbol());
        assertEquals(model.activePlayer(), model.activePlayer().activeLabel().owner());
    }

    @Test
    @DisplayName("Тест №5: пас уменьшает лимит и второй пас с тем же игроком запрещён")
    void passLimitEnforced() {
        Player p = model.activePlayer();

        model.passTurn();
        model.activePlayer().setLabelTo(new Point(1, 1));

        assertSame(p, model.activePlayer());
        assertThrows(IllegalStateException.class, () -> model.passTurn());
    }

    @Test
    @DisplayName("Тест №6: лимит паса сбрасывается при новой игре")
    void passLimitResetsOnNewGame() {
        model.passTurn();
        model.start();
        assertDoesNotThrow(() -> model.passTurn());
    }

    @Test
    @DisplayName("Тест №7: событие labelIsReceived проксируется активному игроку")
    void labelIsReceivedProxied() {
        AtomicInteger cnt = new AtomicInteger();
        model.addPlayerActionListener(new PlayerActionListener() {
            public void labelIsPlaced(PlayerActionEvent e) {}
            public void labelIsReceived(PlayerActionEvent e) { cnt.incrementAndGet(); }
        });
        model.start();
        assertTrue(cnt.get() >= 1);
    }

    @Test
    @DisplayName("Тест №8: событие labelisPlaced проксируется при ходе активного игрока")
    void labelIsPlacedProxied() {
        AtomicInteger cnt = new AtomicInteger();
        model.addPlayerActionListener(new PlayerActionListener() {
            public void labelIsPlaced(PlayerActionEvent e) { cnt.incrementAndGet(); }
            public void labelIsReceived(PlayerActionEvent e) {}
        });
        model.activePlayer().setLabelTo(new Point(1, 1));
        assertEquals(1, cnt.get());
    }

    @Test
    @DisplayName("Тест №9: победа фиксируется при линии из 5 меток")
    void winnerDetected() {
        AtomicReference<Player> winnerRef = new AtomicReference<>();
        model.addGameListener(new GameListener() {
            public void gameFinished(GameEvent e) { winnerRef.set(e.player()); }
            public void playerExchanged(GameEvent e) {}
        });

        for (int x = 1; x <= 5; x++) {
            Player cur = model.activePlayer();
            cur.setLabelTo(new Point(x, 1));
            if (x < 5) {
                model.activePlayer().setLabelTo(new Point(x, 2));
            }
        }
        assertNotNull(winnerRef.get());
    }

    @Test
    @DisplayName("Тест №10: защита от паса без активной метки")
    void passWithoutActiveLabelThrows() {
        model.activePlayer().takeActiveLabel();
        assertThrows(IllegalStateException.class, () -> model.passTurn());
    }

    @Test
    @DisplayName("Тест №11: выбор скрытого типа метки обновляет активную метку")
    void setActiveLabelTypeUpdatesLabel() {
        model.setActiveLabelType(LabelType.HIDDEN);
        Label label = model.activePlayer().activeLabel();
        assertEquals("?", label.symbol());
        assertEquals(model.activePlayer(), label.owner());
    }

    @Test
    @DisplayName("Тест №12: метод passesLeftFor возвращает актуальный остаток")
    void passesLeftForReturnsActualValue() {
        Player p = model.activePlayer();
        assertEquals(1, model.passesLeftFor(p));
        model.passTurn();
        model.activePlayer().setLabelTo(new Point(1, 1));
        assertEquals(0, model.passesLeftFor(p));
    }

    @Test
    @DisplayName("Тест №13: выбор типа метки привязан к игроку и сохраняется между ходами")
    void labelTypeStoredPerPlayer() {
        model.setActiveLabelType(LabelType.HIDDEN);
        assertEquals(LabelType.HIDDEN, model.activeLabelType());

        model.activePlayer().setLabelTo(new Point(1, 1)); // переходим к другому игроку
        assertEquals(LabelType.NORMAL, model.activeLabelType());

        model.activePlayer().setLabelTo(new Point(2, 1)); // возвращаемся к первому
        assertEquals(LabelType.HIDDEN, model.activeLabelType());
        assertEquals("?", model.activePlayer().activeLabel().symbol());
    }

    @Test
    @DisplayName("Тест №14: setActiveLabelType не принимает null")
    void cannotSetNullLabelType() {
        assertThrows(IllegalArgumentException.class, () -> model.setActiveLabelType(null));
    }

    @Test
    @DisplayName("Тест №15: passesLeftFor возвращает 0 для неизвестного игрока")
    void passesLeftForUnknownPlayerIsZero() {
        assertEquals(0, model.passesLeftFor(new Player(model.field(), "Z")));
    }

    @Test
    @DisplayName("Тест №16: старт сбрасывает выбранные типы меток обоих игроков")
    void startResetsLabelTypes() {
        model.setActiveLabelType(LabelType.HIDDEN);
        model.activePlayer().setLabelTo(new Point(1, 1));
        model.setActiveLabelType(LabelType.DELEGATED);

        model.start();

        assertEquals(LabelType.NORMAL, model.activeLabelType());
        assertEquals(model.activePlayer(), model.activePlayer().activeLabel().owner());
        model.activePlayer().setLabelTo(new Point(1, 2));
        assertEquals(LabelType.NORMAL, model.activeLabelType());
    }

    @Test
    @DisplayName("Тест №17: лимит пасов расходуется только у передающего игрока")
    void passConsumesOnlyCurrentPlayerLimit() {
        Player first = model.activePlayer();
        model.passTurn();

        Player second = model.activePlayer();
        assertEquals(1, model.passesLeftFor(second));

        second.setLabelTo(new Point(1, 1));
        assertEquals(0, model.passesLeftFor(first));
        assertEquals(1, model.passesLeftFor(second));
    }

    @Test
    @DisplayName("Тест №18: смена типа метки выдаёт новую активную метку")
    void changingLabelTypeReissuesActiveLabel() {
        Label currentLabel = model.activePlayer().activeLabel();
        model.setActiveLabelType(LabelType.HIDDEN);
        Label refreshed = model.activePlayer().activeLabel();

        assertNotSame(currentLabel, refreshed);
        assertEquals("?", refreshed.symbol());
    }

    @Test
    @DisplayName("Тест №19: повторный старт очищает поле и создаёт новое")
    void startResetsFieldState() {
        model.activePlayer().setLabelTo(new Point(1, 1));
        model.start();

        assertEquals(5, model.field().width());
        assertEquals(5, model.field().height());
        assertNull(model.field().label(new Point(1, 1)));
    }

    @Test
    @DisplayName("Тест №20: после двух ходов ход возвращается первому игроку")
    void turnCyclesBetweenPlayers() {
        Player first = model.activePlayer();
        model.activePlayer().setLabelTo(new Point(1, 1));
        model.activePlayer().setLabelTo(new Point(1, 2));
        assertSame(first, model.activePlayer());
    }

    @Test
    @DisplayName("Тест №21: passTurn генерирует playerExchanged")
    void passTurnFiresPlayerExchanged() {
        AtomicInteger cnt = new AtomicInteger();
        model.addGameListener(new GameListener() {
            public void gameFinished(GameEvent e) {}
            public void playerExchanged(GameEvent e) { cnt.incrementAndGet(); }
        });

        int before = cnt.get();
        model.passTurn();
        assertEquals(before + 1, cnt.get());
    }

    @Test
    @DisplayName("Тест №22: делегированная метка принадлежит сопернику")
    void delegatedLabelBelongsToOpponent() {
        model.setActiveLabelType(LabelType.DELEGATED);
        Label label = model.activePlayer().activeLabel();

        assertNotEquals(model.activePlayer(), label.owner());
        assertEquals("O", label.owner().name());
    }

    @Test
    @DisplayName("Тест №23: при передаче хода активируется тип метки нового игрока")
    void passTurnRestoresNextPlayerLabelType() {
        model.setActiveLabelType(LabelType.HIDDEN);
        model.passTurn();

        assertEquals(LabelType.NORMAL, model.activeLabelType());
        assertEquals("O", model.activePlayer().activeLabel().symbol());
    }

    @Test
    @DisplayName("Тест №24: рестарт возвращает лимит пасов обоим игрокам")
    void restartRestoresPassesForAll() {
        Player first = model.activePlayer();
        model.passTurn();
        model.activePlayer().setLabelTo(new Point(1, 1));
        model.start();

        assertEquals(1, model.passesLeftFor(first));
        assertEquals(1, model.passesLeftFor(model.activePlayer()));
    }

    @Test
    @DisplayName("Тест №25: победа определяется по диагонали из пяти меток")
    void winnerDetectedOnDiagonal() {
        AtomicReference<Player> winnerRef = new AtomicReference<>();
        model.addGameListener(new GameListener() {
            public void gameFinished(GameEvent e) { winnerRef.set(e.player()); }
            public void playerExchanged(GameEvent e) {}
        });

        for (int i = 1; i <= 5; i++) {
            Player current = model.activePlayer();
            current.setLabelTo(new Point(i, i));
            if (i < 5) {
                model.activePlayer().setLabelTo(new Point(i, 5));
            }
        }

        assertNotNull(winnerRef.get());
        assertEquals("X", winnerRef.get().name());
    }
}
