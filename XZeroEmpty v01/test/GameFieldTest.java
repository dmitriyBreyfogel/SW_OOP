import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xzero.model.Cell;
import xzero.model.GameField;
import xzero.model.Label;
import xzero.model.Player;

import java.awt.Point;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GameField: установка меток, границы, линии и мощность связей")
class GameFieldTest {

    private GameField makeField(int w, int h) {
        GameField f = new GameField();
        f.setSize(w,h);
        for (int y=1; y<=h; y++) {
            for (int x=1; x<=w; x++) {
                Cell c = new Cell();
                c.setField(f); c.setPosition(new Point(x,y));
                f.setCell(new Point(x,y), c);
            }
        }
        return f;
    }

    @Test @DisplayName("Тест №1: установка метки в границах поля")
    void setLabelInside() {
        GameField f = makeField(3,3);
        Label l = new Label();
        f.setLabel(new Point(2,2), l);
        assertEquals(l, f.label(new Point(2,2)));
        assertEquals(1, f.labels().size());
    }

    @Test @DisplayName("Тест №2: запрет установки за пределами поля")
    void setLabelOutOfRange() {
        GameField f = makeField(3,3);
        assertThrows(IndexOutOfBoundsException.class,
                () -> f.setLabel(new Point(4,1), new Label()));
    }

    @Test @DisplayName("Тест №3: запрет если ячейка отсутствует (ошибка конфигурации)")
    void setLabelToMissingCell() {
        GameField f = new GameField();
        f.setSize(2,2); // ячейки не добавляли
        assertThrows(IllegalStateException.class,
                () -> f.setLabel(new Point(1,1), new Label()));
    }

    @Test @DisplayName("Тест №4: одна метка не может быть установлена в две разные позиции")
    void oneLabelCannotBeInTwoCells() {
        GameField f = makeField(3,3);
        Label l = new Label();
        f.setLabel(new Point(1,1), l);
        assertThrows(IllegalStateException.class,
                () -> f.setLabel(new Point(2,2), l));
    }

    @Test @DisplayName("Тест №5: нельзя положить метку в занятую ячейку")
    void cannotOverwriteOccupiedCell() {
        GameField f = makeField(3,3);
        f.setLabel(new Point(1,1), new Label());
        assertThrows(IllegalStateException.class,
                () -> f.setLabel(new Point(1,1), new Label()));
    }

    @Test @DisplayName("Тест №6: labels() возвращает немодифицируемый список")
    void labelsIsUnmodifiable() {
        GameField f = makeField(3,3);
        f.setLabel(new Point(1,1), new Label());
        List<Label> ls = f.labels();
        assertThrows(UnsupportedOperationException.class, () -> ls.add(new Label()));
    }

    @Test @DisplayName("Тест №7: labelLine — горизонтальная линия одного игрока")
    void labelLineHorizontal() {
        GameField f = makeField(5,1);
        Player p = new Player(f,"X");
        for (int x=1; x<=5; x++) {
            Label l = new Label(); l.setPlayer(p);
            f.setLabel(new Point(x,1), l);
        }
        List<Label> line = f.labelLine(new Point(1,1), xzero.model.navigation.Direction.east());
        assertEquals(5, line.size());
        assertTrue(line.stream().allMatch(l -> l.player().equals(p)));
    }

    @Test @DisplayName("Тест №8: labelLine обрывается на метке другого игрока")
    void labelLineStopsOnOtherPlayer() {
        GameField f = makeField(5,1);
        Player p1 = new Player(f,"X"); Player p2 = new Player(f,"O");
        for (int x=1; x<=3; x++) { Label l = new Label(); l.setPlayer(p1); f.setLabel(new Point(x,1), l); }
        { Label l = new Label(); l.setPlayer(p2); f.setLabel(new Point(4,1), l); }
        List<Label> line = f.labelLine(new Point(1,1), xzero.model.navigation.Direction.east());
        assertEquals(3, line.size());
    }

    @Test @DisplayName("Тест №9: containsRange корректно проверяет границы")
    void containsRangeBoundary() {
        GameField f = makeField(3,3);
        assertTrue(f.containsRange(new Point(1,1)));
        assertTrue(f.containsRange(new Point(3,3)));
        assertFalse(f.containsRange(new Point(0,1)));
        assertFalse(f.containsRange(new Point(1,4)));
    }

    @Test @DisplayName("Тест №10: setSize удаляет ячейки вне диапазона")
    void setSizeTrimsCells() {
        GameField f = makeField(3,3);
        f.setSize(2,2);
        assertNull(f.label(new Point(3,3)));
    }
}
