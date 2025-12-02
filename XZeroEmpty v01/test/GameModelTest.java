import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xzero.model.GameModel;
import xzero.model.labels.Label;
import xzero.model.Player;
import xzero.model.events.GameEvent;
import xzero.model.events.GameListener;
import xzero.model.events.PlayerActionEvent;
import xzero.model.events.PlayerActionListener;

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
        // стартуем поле и игроков
        model.start();
    }

    @Test @DisplayName("Тест №1: после старта активный игрок получает метку")
    void activePlayerGetsLabelOnStart() {
        assertNotNull(model.activePlayer().activeLabel());
    }

    @Test @DisplayName("Тест №2: событие playerExchanged приходит при старте")
    void playerExchangedOnStart() {
        AtomicReference<Player> last = new AtomicReference<>();
        model.addGameListener(new GameListener() {
            public void gameFinished(GameEvent e) {}
            public void playerExchanged(GameEvent e) { last.set(e.player()); }
        });
        model.start(); // снова стартуем новую партию
        assertNotNull(last.get());
    }

    @Test @DisplayName("Тест №3: после установки метки ход переходит другому игроку")
    void turnChangesAfterPlacement() {
        Player first = model.activePlayer();
        Point p = new Point(1,1);
        model.activePlayer().setLabelTo(p);
        Player second = model.activePlayer();
        assertNotSame(first, second);
        assertNotNull(model.activePlayer().activeLabel());
    }

    @Test @DisplayName("Тест №4: пас передаёт ту же активную метку противнику")
    void passTransfersSameLabel() {
        Label l = model.activePlayer().activeLabel();
        model.passTurn();
        assertSame(l, model.activePlayer().activeLabel());
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

    @Test @DisplayName("Тест №6: лимит паса сбрасывается при новой игре")
    void passLimitResetsOnNewGame() {
        model.passTurn();
        model.start();
        assertDoesNotThrow(() -> model.passTurn());
    }

    @Test @DisplayName("Тест №7: событие labelIsReceived проксируется активному игроку")
    void labelIsReceivedProxied() {
        AtomicInteger cnt = new AtomicInteger();
        model.addPlayerActionListener(new PlayerActionListener() {
            public void labelisPlaced(PlayerActionEvent e) {}
            public void labelIsReceived(PlayerActionEvent e) { cnt.incrementAndGet(); }
        });
        model.start();
        assertTrue(cnt.get() >= 1);
    }

    @Test @DisplayName("Тест №8: событие labelisPlaced проксируется при ходе активного игрока")
    void labelIsPlacedProxied() {
        AtomicInteger cnt = new AtomicInteger();
        model.addPlayerActionListener(new PlayerActionListener() {
            public void labelisPlaced(PlayerActionEvent e) { cnt.incrementAndGet(); }
            public void labelIsReceived(PlayerActionEvent e) {}
        });
        model.activePlayer().setLabelTo(new Point(1,1));
        assertEquals(1, cnt.get());
    }

    @Test @DisplayName("Тест №9: победа фиксируется при линии из 5 меток")
    void winnerDetected() {
        AtomicReference<Player> winnerRef = new AtomicReference<>();
        model.addGameListener(new GameListener() {
            public void gameFinished(GameEvent e) { winnerRef.set(e.player()); }
            public void playerExchanged(GameEvent e) {}
        });

        for (int x=1; x<=5; x++) {
            Player cur = model.activePlayer();
            cur.setLabelTo(new Point(x,1));
            if (x<5) {
                model.activePlayer().setLabelTo(new Point(x,2));
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
}
