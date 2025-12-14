import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xzero.model.Cell;
import xzero.model.GameField;
import xzero.model.Player;
import xzero.model.events.PlayerActionEvent;
import xzero.model.events.PlayerActionListener;
import xzero.model.labels.Label;
import xzero.model.labels.NormalLabel;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Player: активная метка, события и установка на поле")
class PlayerTest {

    private GameField preparedField(int w, int h) {
        GameField f = new GameField();
        f.setSize(w, h);
        for (int y = 1; y <= h; y++) {
            for (int x = 1; x <= w; x++) {
                Cell c = new Cell();
                c.setField(f);
                c.setPosition(new Point(x, y));
                f.setCell(new Point(x, y), c);
            }
        }
        return f;
    }

    private Label labelFor(GameField f, Player owner) {
        return new NormalLabel(owner);
    }

    @Test
    @DisplayName("Тест №1: выдача активной метки фиксирует выдавшего игрока")
    void setActiveLabelAssignsPlayer() {
        GameField f = preparedField(3, 3);
        Player p = new Player(f, "X");
        Label l = labelFor(f, p);
        p.setActiveLabel(l);
        assertEquals(p, l.getPlacedBy());
        assertEquals(l, p.activeLabel());
    }

    @Test
    @DisplayName("Тест №2: нельзя установить метку без активной метки")
    void cannotSetWithoutActive() {
        GameField f = preparedField(3, 3);
        Player p = new Player(f, "X");
        assertThrows(IllegalStateException.class, () -> p.setLabelTo(new Point(1, 1)));
    }

    @Test
    @DisplayName("Тест №3: установка метки кладёт её в ячейку")
    void setLabelPlacesToCell() {
        GameField f = preparedField(3, 3);
        Player p = new Player(f, "X");
        Label l = labelFor(f, p);
        p.setActiveLabel(l);
        p.setLabelTo(new Point(2, 2));
        assertEquals(l, f.label(new Point(2, 2)));
        assertNull(p.activeLabel()); // метка израсходована
    }

    @Test
    @DisplayName("Тест №4: событие labelIsReceived вызывается при выдаче метки")
    void firesLabelReceived() {
        GameField f = preparedField(3, 3);
        Player p = new Player(f, "X");
        AtomicInteger cnt = new AtomicInteger();

        p.addPlayerActionListener(new PlayerActionListener() {
            @Override
            public void labelIsPlaced(PlayerActionEvent event) {
            }

            @Override
            public void labelIsReceived(PlayerActionEvent event) {
                cnt.incrementAndGet();
            }
        });

        Label l = labelFor(f, p);
        p.setActiveLabel(l);
        assertEquals(1, cnt.get());
    }

    @Test
    @DisplayName("Тест №5: событие labelisPlaced вызывается при установке метки")
    void firesLabelPlaced() {
        GameField f = preparedField(3, 3);
        Player p = new Player(f, "X");
        AtomicInteger cnt = new AtomicInteger();

        p.addPlayerActionListener(new PlayerActionListener() {
            @Override
            public void labelIsPlaced(PlayerActionEvent event) {
                cnt.incrementAndGet();
            }

            @Override
            public void labelIsReceived(PlayerActionEvent event) {
            }
        });

        Label l = labelFor(f, p);
        p.setActiveLabel(l);
        p.setLabelTo(new Point(1, 1));
        assertEquals(1, cnt.get());
    }

    @Test
    @DisplayName("Тест №6: labels() возвращает только метки этого игрока")
    void labelsReturnsOnlyOwn() {
        GameField f = preparedField(3, 3);
        Player p1 = new Player(f, "X");
        Player p2 = new Player(f, "O");
        Label l1 = labelFor(f, p1);
        Label l2 = labelFor(f, p2);
        f.setLabel(new Point(1, 1), l1);
        f.setLabel(new Point(2, 1), l2);
        assertEquals(1, p1.labels().size());
        assertEquals(l1, p1.labels().get(0));
    }

    @Test
    @DisplayName("Тест №7: takeActiveLabel возвращает метку и чистит активную")
    void takeActiveReturnsAndClears() {
        GameField f = preparedField(3, 3);
        Player p = new Player(f, "X");
        Label l = labelFor(f, p);
        p.setActiveLabel(l);
        Label taken = p.takeActiveLabel();
        assertSame(l, taken);
        assertNull(p.activeLabel());
    }

    @Test
    @DisplayName("Тест №8: takeActiveLabel без метки — исключение")
    void takeActiveWithoutLabelThrows() {
        GameField f = preparedField(3, 3);
        Player p = new Player(f, "X");
        assertThrows(IllegalStateException.class, p::takeActiveLabel);
    }

    @Test
    @DisplayName("Тест №9: выдача null-метки запрещена")
    void cannotReceiveNullLabel() {
        GameField f = preparedField(3, 3);
        Player p = new Player(f, "X");
        assertThrows(IllegalArgumentException.class, () -> p.setActiveLabel(null));
    }

    @Test
    @DisplayName("Тест №10: удалённый слушатель не получает события")
    void removedListenerDoesNotFire() {
        GameField f = preparedField(3, 3);
        Player p = new Player(f, "X");
        AtomicInteger cnt = new AtomicInteger();

        PlayerActionListener listener = new PlayerActionListener() {
            @Override
            public void labelIsPlaced(PlayerActionEvent event) {
                cnt.incrementAndGet();
            }

            @Override
            public void labelIsReceived(PlayerActionEvent event) {
                cnt.incrementAndGet();
            }
        };

        p.addPlayerActionListener(listener);
        p.removePlayerActionListener(listener);

        p.setActiveLabel(labelFor(f, p));
        p.setLabelTo(new Point(1, 1));

        assertEquals(0, cnt.get());
    }

    @Test
    @DisplayName("Тест №11: имя игрока можно изменить")
    void playerNameCanBeChanged() {
        GameField f = preparedField(1, 1);
        Player p = new Player(f, "X");

        p.setName("Новое имя");

        assertEquals("Новое имя", p.name());
    }

    @Test
    @DisplayName("Тест №12: setLabelTo сохраняет ссылку на игрока, выдавшего метку")
    void placedLabelKeepsIssuer() {
        GameField f = preparedField(2, 2);
        Player p = new Player(f, "X");

        p.setActiveLabel(labelFor(f, p));
        p.setLabelTo(new Point(1, 1));

        Label placed = f.label(new Point(1, 1));
        assertEquals(p, placed.getPlacedBy());
    }

    @Test
    @DisplayName("Тест №13: список labels немодифицируемый")
    void labelsListIsUnmodifiable() {
        GameField f = preparedField(2, 2);
        Player p = new Player(f, "X");
        f.setLabel(new Point(1, 1), labelFor(f, p));

        assertThrows(UnsupportedOperationException.class, () -> p.labels().add(labelFor(f, p)));
    }

    @Test
    @DisplayName("Тест №14: добавление null-слушателя безопасно")
    void addingNullListenerIsSafe() {
        GameField f = preparedField(1, 1);
        Player p = new Player(f, "X");
        assertDoesNotThrow(() -> p.addPlayerActionListener(null));
    }

    @Test
    @DisplayName("Тест №15: takeActiveLabel сохраняет информацию о выдавшем")
    void takeActiveLabelKeepsPlacedBy() {
        GameField f = preparedField(1, 1);
        Player p = new Player(f, "X");
        Label l = labelFor(f, p);
        p.setActiveLabel(l);
        Label taken = p.takeActiveLabel();

        assertEquals(p, taken.getPlacedBy());
    }

    @Test
    @DisplayName("Тест №16: нельзя второй раз ставить уже израсходованную метку")
    void cannotPlaceConsumedLabelTwice() {
        GameField f = preparedField(2, 2);
        Player p = new Player(f, "X");
        p.setActiveLabel(labelFor(f, p));
        p.setLabelTo(new Point(1, 1));
        assertThrows(IllegalStateException.class, () -> p.setLabelTo(new Point(2, 2)));
    }

    @Test
    @DisplayName("Тест №17: попытка поставить метку за границы поля вызывает исключение")
    void cannotPlaceOutsideField() {
        GameField f = preparedField(2, 2);
        Player p = new Player(f, "X");
        p.setActiveLabel(labelFor(f, p));
        assertThrows(IndexOutOfBoundsException.class, () -> p.setLabelTo(new Point(3, 3)));
    }
}
