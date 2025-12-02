import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xzero.model.Cell;
import xzero.model.GameField;
import Label;
import xzero.model.Player;
import xzero.model.events.PlayerActionEvent;
import xzero.model.events.PlayerActionListener;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Player: активная метка, события и установка на поле")
class PlayerTest {

    private GameField preparedField(int w, int h) {
        GameField f = new GameField(); f.setSize(w,h);
        for (int y=1; y<=h; y++) for (int x=1; x<=w; x++) {
            Cell c = new Cell(); c.setField(f); c.setPosition(new Point(x,y));
            f.setCell(new Point(x,y), c);
        }
        return f;
    }

    @Test @DisplayName("Тест №1: выдача активной метки — метка знает игрока")
    void setActiveLabelAssignsPlayer() {
        GameField f = preparedField(3,3);
        Player p = new Player(f,"X");
        Label l = new Label();
        p.setActiveLabel(l);
        assertEquals(p, l.player());
        assertEquals(l, p.activeLabel());
    }

    @Test @DisplayName("Тест №2: нельзя установить метку без активной метки")
    void cannotSetWithoutActive() {
        GameField f = preparedField(3,3);
        Player p = new Player(f,"X");
        assertThrows(IllegalStateException.class, () -> p.setLabelTo(new Point(1,1)));
    }

    @Test @DisplayName("Тест №3: установка метки кладёт её в ячейку")
    void setLabelPlacesToCell() {
        GameField f = preparedField(3,3);
        Player p = new Player(f,"X");
        Label l = new Label(); p.setActiveLabel(l);
        p.setLabelTo(new Point(2,2));
        assertEquals(l, f.label(new Point(2,2)));
        assertNull(p.activeLabel()); // метка израсходована
    }

    @Test
    @DisplayName("Тест №4: событие labelIsReceived вызывается при выдаче метки")
    void firesLabelReceived() {
        GameField f = preparedField(3,3);
        Player p = new Player(f,"X");
        AtomicInteger cnt = new AtomicInteger();

        p.addPlayerActionListener(new PlayerActionListener() {
            @Override
            public void labelisPlaced(PlayerActionEvent e) {
            }
            @Override
            public void labelIsReceived(PlayerActionEvent e) {
                cnt.incrementAndGet();
            }
        });

        Label l = new Label();
        p.setActiveLabel(l);
        assertEquals(1, cnt.get());
    }

    @Test
    @DisplayName("Тест №5: событие labelisPlaced вызывается при установке метки")
    void firesLabelPlaced() {
        GameField f = preparedField(3,3);
        Player p = new Player(f,"X");
        AtomicInteger cnt = new AtomicInteger();

        p.addPlayerActionListener(new PlayerActionListener() {
            @Override
            public void labelisPlaced(PlayerActionEvent e) {
                cnt.incrementAndGet();
            }
            @Override
            public void labelIsReceived(PlayerActionEvent e) {
            }
        });

        Label l = new Label();
        p.setActiveLabel(l);
        p.setLabelTo(new Point(1,1));
        assertEquals(1, cnt.get());
    }

    @Test @DisplayName("Тест №6: labels() возвращает только метки этого игрока")
    void labelsReturnsOnlyOwn() {
        GameField f = preparedField(3,3);
        Player p1 = new Player(f,"X"); Player p2 = new Player(f,"O");
        Label l1 = new Label(); l1.setPlayer(p1); f.setLabel(new Point(1,1), l1);
        Label l2 = new Label(); l2.setPlayer(p2); f.setLabel(new Point(2,1), l2);
        assertEquals(1, p1.labels().size());
        assertEquals(l1, p1.labels().get(0));
    }

    @Test @DisplayName("Тест №7: takeActiveLabel возвращает метку и чистит активную")
    void takeActiveReturnsAndClears() {
        GameField f = preparedField(3,3);
        Player p = new Player(f,"X");
        Label l = new Label(); p.setActiveLabel(l);
        Label taken = p.takeActiveLabel();
        assertSame(l, taken);
        assertNull(p.activeLabel());
    }

    @Test @DisplayName("Тест №8: takeActiveLabel без метки — исключение")
    void takeActiveWithoutLabelThrows() {
        GameField f = preparedField(3,3);
        Player p = new Player(f,"X");
        assertThrows(IllegalStateException.class, p::takeActiveLabel);
    }

    @Test @DisplayName("Тест №9: выдача null-метки запрещена")
    void cannotReceiveNullLabel() {
        GameField f = preparedField(3,3);
        Player p = new Player(f,"X");
        assertThrows(IllegalArgumentException.class, () -> p.setActiveLabel(null));
    }
}
