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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Player (дополнительно): слушатели, список меток и активная метка")
class PlayerAdditionalTest {

    private GameField filledField(int size) {
        GameField f = new GameField();
        f.setSize(size, size);
        for (int y = 1; y <= size; y++) {
            for (int x = 1; x <= size; x++) {
                Cell c = new Cell();
                c.setField(f);
                c.setPosition(new Point(x, y));
                f.setCell(new Point(x, y), c);
            }
        }
        return f;
    }

    @Test
    @DisplayName("Тест №1: добавление null-слушателя не приводит к ошибкам и не вызывает событий")
    void addingNullListenerIsIgnored() {
        GameField f = filledField(1);
        Player p = new Player(f, "X");
        AtomicInteger calls = new AtomicInteger();

        p.addPlayerActionListener(null);
        p.addPlayerActionListener(new PlayerActionListener() {
            public void labelisPlaced(PlayerActionEvent e) { calls.incrementAndGet(); }
            public void labelIsReceived(PlayerActionEvent e) { calls.incrementAndGet(); }
        });

        p.setActiveLabel(new NormalLabel(p));
        p.setLabelTo(new Point(1, 1));

        assertEquals(2, calls.get());
    }

    @Test
    @DisplayName("Тест №2: список меток игрока немодифицируемый")
    void labelsListIsUnmodifiable() {
        GameField f = filledField(2);
        Player p = new Player(f, "X");
        f.setLabel(new Point(1, 1), new NormalLabel(p));

        List<Label> labels = p.labels();

        assertThrows(UnsupportedOperationException.class, () -> labels.add(new NormalLabel(p)));
    }

    @Test
    @DisplayName("Тест №3: labels пересчитывается при каждом вызове")
    void labelsRecomputedEachTime() {
        GameField f = filledField(2);
        Player p = new Player(f, "X");
        f.setLabel(new Point(1, 1), new NormalLabel(p));

        assertEquals(1, p.labels().size());

        f.setLabel(new Point(2, 1), new NormalLabel(p));

        assertEquals(2, p.labels().size());
    }

    @Test
    @DisplayName("Тест №4: takeActiveLabel возвращает ту же метку, что будет установлена")
    void takeActiveLabelMatchesPlacedLabel() {
        GameField f = filledField(1);
        Player p = new Player(f, "X");
        NormalLabel label = new NormalLabel(p);

        p.setActiveLabel(label);
        Label active = p.takeActiveLabel();
        f.setLabel(new Point(1, 1), active);

        assertSame(label, f.label(new Point(1, 1)));
    }

    @Test
    @DisplayName("Тест №5: установка метки после takeActiveLabel невозможна без новой метки")
    void cannotPlaceAfterTakingActiveLabelWithoutNewOne() {
        GameField f = filledField(1);
        Player p = new Player(f, "X");
        p.setActiveLabel(new NormalLabel(p));
        p.takeActiveLabel();

        assertThrows(IllegalStateException.class, () -> p.setLabelTo(new Point(1, 1)));
    }

    @Test
    @DisplayName("Тест №6: событие размещения включает корректную ссылку на метку")
    void placedEventContainsCorrectLabel() {
        GameField f = filledField(1);
        Player p = new Player(f, "X");
        AtomicInteger matches = new AtomicInteger();
        Label label = new NormalLabel(p);

        p.addPlayerActionListener(new PlayerActionListener() {
            public void labelisPlaced(PlayerActionEvent e) {
                if (e.label() == label) {
                    matches.incrementAndGet();
                }
            }
            public void labelIsReceived(PlayerActionEvent e) { }
        });

        p.setActiveLabel(label);
        p.setLabelTo(new Point(1, 1));

        assertEquals(1, matches.get());
    }

    @Test
    @DisplayName("Тест №7: активная метка не затирается при повторной выдаче без размещения")
    void reassigningActiveLabelWithoutPlacementOverridesOldOne() {
        GameField f = filledField(1);
        Player p = new Player(f, "X");
        NormalLabel first = new NormalLabel(p);
        NormalLabel second = new NormalLabel(p);

        p.setActiveLabel(first);
        p.setActiveLabel(second);

        assertSame(second, p.activeLabel());
        assertNull(first.cell());
    }
}
