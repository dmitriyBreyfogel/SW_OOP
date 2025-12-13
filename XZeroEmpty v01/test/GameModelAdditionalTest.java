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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GameModel (дополнительно): восстановление состояния, победы и события")
class GameModelAdditionalTest {

    private GameModel model;

    @BeforeEach
    void setup() {
        model = new GameModel();
        model.start();
    }

    @Test
    @DisplayName("Тест №1: start очищает поле и выдаёт новую активную метку первому игроку")
    void startResetsFieldAndActiveLabel() {
        model.activePlayer().setLabelTo(new Point(1, 1));
        Label before = model.activePlayer().activeLabel();

        model.start();

        assertEquals(0, model.field().labels().size());
        assertNotSame(before, model.activePlayer().activeLabel());
        assertEquals("X", model.activePlayer().activeLabel().symbol());
    }

    @Test
    @DisplayName("Тест №2: активный игрок после старта всегда первый в списке")
    void firstPlayerStartsAfterReset() {
        model.passTurn();
        model.activePlayer().setLabelTo(new Point(1, 1));
        model.start();

        assertEquals("X", model.activePlayer().name());
    }

    @Test
    @DisplayName("Тест №3: пас отнимает активную метку и выдаёт новую текущему игроку")
    void passConsumesActiveLabelAndIssuesNewOne() {
        Label current = model.activePlayer().activeLabel();
        model.passTurn();

        assertNull(current.cell());
        assertNotNull(model.activePlayer().activeLabel());
        assertEquals("O", model.activePlayer().activeLabel().symbol());
    }

    @Test
    @DisplayName("Тест №4: лимиты пасов обоих игроков сбрасываются при новом старте")
    void passLimitsResetForAllPlayersOnStart() {
        model.passTurn();
        model.activePlayer().setLabelTo(new Point(1, 1));
        model.passTurn();

        model.start();

        Player first = model.activePlayer();
        model.passTurn();
        Player second = model.activePlayer();

        assertEquals(0, model.passesLeftFor(first));
        assertEquals(1, model.passesLeftFor(second));
    }

    @Test
    @DisplayName("Тест №5: победа определяется по диагональной линии")
    void winnerDetectedOnDiagonal() {
        AtomicReference<Player> winner = new AtomicReference<>();
        model.addGameListener(new GameListener() {
            public void gameFinished(GameEvent e) { winner.set(e.player()); }
            public void playerExchanged(GameEvent e) { }
        });

        for (int i = 1; i <= 5; i++) {
            model.activePlayer().setLabelTo(new Point(i, i));
            if (i < 5) {
                model.activePlayer().setLabelTo(new Point(i, i + 1));
            }
        }

        assertEquals("X", winner.get().name());
    }

    @Test
    @DisplayName("Тест №6: делегированная метка приносит победу владельцу, а не ходившему")
    void delegatedLabelCountsForOwner() {
        AtomicReference<Player> winner = new AtomicReference<>();
        model.addGameListener(new GameListener() {
            public void gameFinished(GameEvent e) { winner.set(e.player()); }
            public void playerExchanged(GameEvent e) { }
        });

        model.setActiveLabelType(LabelType.DELEGATED);
        for (int x = 1; x <= 5; x++) {
            Player mover = model.activePlayer();
            mover.setLabelTo(new Point(x, 1));
            if (x < 5) {
                model.activePlayer().setLabelTo(new Point(x, 2));
            }
        }

        assertEquals("O", winner.get().name());
    }

    @Test
    @DisplayName("Тест №7: выбор скрытого типа для второго игрока сохраняется между ходами")
    void hiddenTypePersistedForSecondPlayer() {
        model.activePlayer().setLabelTo(new Point(1, 1));
        model.setActiveLabelType(LabelType.HIDDEN);
        model.activePlayer().setLabelTo(new Point(2, 1));

        model.activePlayer().setLabelTo(new Point(3, 1));

        assertEquals(LabelType.HIDDEN, model.activeLabelType());
        assertEquals("?", model.activePlayer().activeLabel().symbol());
    }

    @Test
    @DisplayName("Тест №8: handleLabelPlaced меняет ход если победителя нет")
    void handleLabelPlacedExchangesPlayerWhenNoWinner() {
        AtomicInteger exchanges = new AtomicInteger();
        model.addGameListener(new GameListener() {
            public void gameFinished(GameEvent e) { }
            public void playerExchanged(GameEvent e) { exchanges.incrementAndGet(); }
        });

        model.activePlayer().setLabelTo(new Point(1, 1));

        assertEquals(1, exchanges.get());
        assertEquals("O", model.activePlayer().name());
    }

    @Test
    @DisplayName("Тест №9: удалённый слушатель игры не получает события")
    void removedGameListenerDoesNotFire() {
        AtomicInteger finished = new AtomicInteger();
        GameListener listener = new GameListener() {
            public void gameFinished(GameEvent e) { finished.incrementAndGet(); }
            public void playerExchanged(GameEvent e) { }
        };

        model.addGameListener(listener);
        model.removeGameListener(listener);

        for (int x = 1; x <= 5; x++) {
            model.activePlayer().setLabelTo(new Point(x, 1));
            if (x < 5) {
                model.activePlayer().setLabelTo(new Point(x, 2));
            }
        }

        assertEquals(0, finished.get());
    }

    @Test
    @DisplayName("Тест №10: оба слушателя действий получают события получения и размещения")
    void multiplePlayerActionListenersReceiveEvents() {
        List<String> log = new ArrayList<>();
        PlayerActionListener l1 = new PlayerActionListener() {
            public void labelisPlaced(PlayerActionEvent e) { log.add("placed1"); }
            public void labelIsReceived(PlayerActionEvent e) { log.add("received1"); }
        };
        PlayerActionListener l2 = new PlayerActionListener() {
            public void labelisPlaced(PlayerActionEvent e) { log.add("placed2"); }
            public void labelIsReceived(PlayerActionEvent e) { log.add("received2"); }
        };

        model.addPlayerActionListener(l1);
        model.addPlayerActionListener(l2);

        model.setActiveLabelType(LabelType.HIDDEN);
        model.activePlayer().setLabelTo(new Point(1, 1));

        assertTrue(log.contains("received1"));
        assertTrue(log.contains("received2"));
        assertTrue(log.contains("placed1"));
        assertTrue(log.contains("placed2"));
    }

    @Test
    @DisplayName("Тест №11: смена типа метки для второго игрока влияет только на его активную метку")
    void labelTypeChangeScopedToCurrentPlayer() {
        model.activePlayer().setLabelTo(new Point(1, 1));
        model.setActiveLabelType(LabelType.DELEGATED);
        Label delegated = model.activePlayer().activeLabel();

        model.activePlayer().setLabelTo(new Point(2, 1));

        assertEquals(LabelType.NORMAL, model.activeLabelType());
        assertEquals("O", delegated.symbol());
        assertEquals("X", model.activePlayer().activeLabel().symbol());
    }

    @Test
    @DisplayName("Тест №12: passTurn сохраняет выбранный тип метки следующего игрока")
    void passTurnRestoresNextPlayerTypePreference() {
        model.setActiveLabelType(LabelType.HIDDEN);
        model.passTurn();
        model.setActiveLabelType(LabelType.DELEGATED);

        model.passTurn();

        assertEquals(LabelType.HIDDEN, model.activeLabelType());
        assertEquals("?", model.activePlayer().activeLabel().symbol());
    }

    @Test
    @DisplayName("Тест №13: попытка победы без пятой метки не завершает игру")
    void incompleteLineDoesNotFinishGame() {
        AtomicInteger finished = new AtomicInteger();
        model.addGameListener(new GameListener() {
            public void gameFinished(GameEvent e) { finished.incrementAndGet(); }
            public void playerExchanged(GameEvent e) { }
        });

        for (int x = 1; x <= 4; x++) {
            model.activePlayer().setLabelTo(new Point(x, 1));
            model.activePlayer().setLabelTo(new Point(x, 2));
        }

        assertEquals(0, finished.get());
    }

    @Test
    @DisplayName("Тест №14: passesLeftFor защищает от отрицательных индексов неизвестного игрока")
    void passesLeftForUnknownPlayerStillZero() {
        Player alien = new Player(model.field(), "Z");
        assertEquals(0, model.passesLeftFor(alien));
    }

    @Test
    @DisplayName("Тест №15: setActiveLabelType выдаёт новую метку при смене на делегированную")
    void changingTypeToDelegatedIssuesNewLabel() {
        Label current = model.activePlayer().activeLabel();
        model.setActiveLabelType(LabelType.DELEGATED);
        Label delegated = model.activePlayer().activeLabel();

        assertNotSame(current, delegated);
        assertEquals("O", delegated.symbol());
    }
}
