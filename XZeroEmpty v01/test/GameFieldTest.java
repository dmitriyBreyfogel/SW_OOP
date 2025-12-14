import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xzero.model.Cell;
import xzero.model.GameField;
import xzero.model.Player;
import xzero.model.labels.Label;
import xzero.model.labels.NormalLabel;
import xzero.model.navigation.Direction;

import java.awt.Point;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GameField: установка меток, границы, линии и мощность связей")
class GameFieldTest {

    private GameField makeField(int w, int h) {
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
    @DisplayName("Тест №1: установка метки в границах поля")
    void setLabelInside() {
        GameField f = makeField(3, 3);
        Label l = labelFor(f, new Player(f, "X"));
        f.setLabel(new Point(2, 2), l);
        assertEquals(l, f.label(new Point(2, 2)));
        assertEquals(1, f.labels().size());
    }

    @Test
    @DisplayName("Тест №2: запрет установки за пределами поля")
    void setLabelOutOfRange() {
        GameField f = makeField(3, 3);
        assertThrows(IndexOutOfBoundsException.class,
                () -> f.setLabel(new Point(4, 1), labelFor(f, new Player(f, "X"))));
    }

    @Test
    @DisplayName("Тест №3: запрет если ячейка отсутствует (ошибка конфигурации)")
    void setLabelToMissingCell() {
        GameField f = new GameField();
        f.setSize(2, 2); // ячейки не добавляли
        assertThrows(IllegalStateException.class,
                () -> f.setLabel(new Point(1, 1), labelFor(f, new Player(f, "X"))));
    }

    @Test
    @DisplayName("Тест №4: одна метка не может быть установлена в две разные позиции")
    void oneLabelCannotBeInTwoCells() {
        GameField f = makeField(3, 3);
        Label l = labelFor(f, new Player(f, "X"));
        f.setLabel(new Point(1, 1), l);
        assertThrows(IllegalStateException.class,
                () -> f.setLabel(new Point(2, 2), l));
    }

    @Test
    @DisplayName("Тест №5: нельзя положить метку в занятую ячейку")
    void cannotOverwriteOccupiedCell() {
        GameField f = makeField(3, 3);
        f.setLabel(new Point(1, 1), labelFor(f, new Player(f, "X")));
        assertThrows(IllegalStateException.class,
                () -> f.setLabel(new Point(1, 1), labelFor(f, new Player(f, "O"))));
    }

    @Test
    @DisplayName("Тест №6: labels() возвращает немодифицируемый список")
    void labelsIsUnmodifiable() {
        GameField f = makeField(3, 3);
        f.setLabel(new Point(1, 1), labelFor(f, new Player(f, "X")));
        List<Label> ls = f.labels();
        assertThrows(UnsupportedOperationException.class, () -> ls.add(labelFor(f, new Player(f, "O"))));
    }

    @Test
    @DisplayName("Тест №7: labelLine — горизонтальная линия одного игрока")
    void labelLineHorizontal() {
        GameField f = makeField(5, 1);
        Player p = new Player(f, "X");
        for (int x = 1; x <= 5; x++) {
            f.setLabel(new Point(x, 1), labelFor(f, p));
        }
        List<Label> line = f.labelLine(new Point(1, 1), Direction.east());
        assertEquals(5, line.size());
        assertTrue(line.stream().allMatch(l -> l.owner().equals(p)));
    }

    @Test
    @DisplayName("Тест №8: labelLine обрывается на метке другого игрока")
    void labelLineStopsOnOtherPlayer() {
        GameField f = makeField(5, 1);
        Player p1 = new Player(f, "X");
        Player p2 = new Player(f, "O");
        for (int x = 1; x <= 3; x++) {
            f.setLabel(new Point(x, 1), labelFor(f, p1));
        }
        f.setLabel(new Point(4, 1), labelFor(f, p2));
        List<Label> line = f.labelLine(new Point(1, 1), Direction.east());
        assertEquals(3, line.size());
    }

    @Test
    @DisplayName("Тест №9: containsRange корректно проверяет границы")
    void containsRangeBoundary() {
        GameField f = makeField(3, 3);
        assertTrue(f.containsRange(new Point(1, 1)));
        assertTrue(f.containsRange(new Point(3, 3)));
        assertFalse(f.containsRange(new Point(0, 1)));
        assertFalse(f.containsRange(new Point(1, 4)));
    }

    @Test
    @DisplayName("Тест №10: setSize удаляет ячейки вне диапазона")
    void setSizeTrimsCells() {
        GameField f = makeField(3, 3);
        f.setSize(2, 2);
        assertNull(f.label(new Point(3, 3)));
    }

    @Test
    @DisplayName("Тест №11: clear очищает все ячейки и метки")
    void clearRemovesAllCellsAndLabels() {
        GameField f = makeField(2, 2);
        Label l = labelFor(f, new Player(f, "X"));
        f.setLabel(new Point(1, 1), l);

        assertEquals(1, f.labels().size());

        f.clear();

        assertEquals(0, f.labels().size());
        assertNull(f.label(new Point(1, 1)));
    }

    @Test
    @DisplayName("Тест №12: установка null-метки запрещена")
    void cannotSetNullLabel() {
        GameField f = makeField(2, 2);
        assertThrows(IllegalArgumentException.class, () -> f.setLabel(new Point(1, 1), null));
    }

    @Test
    @DisplayName("Тест №13: диагональная линия строится корректно")
    void labelLineDiagonal() {
        GameField f = makeField(3, 3);
        Player p = new Player(f, "X");
        f.setLabel(new Point(1, 1), labelFor(f, p));
        f.setLabel(new Point(2, 2), labelFor(f, p));
        f.setLabel(new Point(3, 3), labelFor(f, p));

        List<Label> line = f.labelLine(new Point(1, 1), Direction.southEast());
        assertEquals(3, line.size());
        assertTrue(line.stream().allMatch(l -> l.owner().equals(p)));
    }

    @Test
    @DisplayName("Тест №14: пустая стартовая ячейка возвращает пустую линию")
    void labelLineFromEmptyCellIsEmpty() {
        GameField f = makeField(2, 2);
        List<Label> line = f.labelLine(new Point(1, 1), Direction.east());
        assertTrue(line.isEmpty());
    }

    @Test
    @DisplayName("Тест №15: линия обрывается на пустой ячейке")
    void labelLineStopsOnEmptyCell() {
        GameField f = makeField(4, 1);
        Player p = new Player(f, "X");
        f.setLabel(new Point(1, 1), labelFor(f, p));
        f.setLabel(new Point(3, 1), labelFor(f, p));

        List<Label> line = f.labelLine(new Point(1, 1), Direction.east());
        assertEquals(1, line.size());
    }

    @Test
    @DisplayName("Тест №16: вертикальная линия строится корректно")
    void labelLineVertical() {
        GameField f = makeField(1, 4);
        Player p = new Player(f, "X");
        for (int y = 1; y <= 4; y++) {
            f.setLabel(new Point(1, y), labelFor(f, p));
        }
        List<Label> line = f.labelLine(new Point(1, 1), Direction.south());
        assertEquals(4, line.size());
    }

    @Test
    @DisplayName("Тест №17: setSize меняет ширину и высоту поля")
    void setSizeUpdatesDimensions() {
        GameField f = new GameField();
        f.setSize(7, 8);
        assertEquals(7, f.width());
        assertEquals(8, f.height());
    }

    @Test
    @DisplayName("Тест №18: попытка установки метки в удалённую ячейку после resize запрещена")
    void cannotSetLabelAfterCellTrimmedByResize() {
        GameField f = makeField(3, 3);
        f.setSize(2, 2);
        Player p = new Player(f, "X");
        assertThrows(IllegalStateException.class,
                () -> f.setLabel(new Point(3, 3), labelFor(f, p)));
    }

    @Test
    @DisplayName("Тест №19: setCell замещает существующую ячейку на позиции")
    void setCellReplacesPrevious() {
        GameField f = new GameField();
        f.setSize(1, 1);
        Cell first = new Cell();
        first.setField(f);
        first.setPosition(new Point(1, 1));
        f.setCell(new Point(1, 1), first);

        Cell second = new Cell();
        second.setField(f);
        second.setPosition(new Point(1, 1));
        f.setCell(new Point(1, 1), second);

        f.setLabel(new Point(1, 1), labelFor(f, new Player(f, "X")));

        assertNull(first.label());
        assertSame(second, f.label(new Point(1, 1)).cell());
    }

    @Test
    @DisplayName("Тест №20: диагональ на северо-запад строится корректно")
    void labelLineNorthWest() {
        GameField f = makeField(3, 3);
        Player p = new Player(f, "X");
        f.setLabel(new Point(3, 3), labelFor(f, p));
        f.setLabel(new Point(2, 2), labelFor(f, p));
        f.setLabel(new Point(1, 1), labelFor(f, p));

        List<Label> line = f.labelLine(new Point(3, 3), Direction.northWest());
        assertEquals(3, line.size());
    }

    @Test
    @DisplayName("Тест №21: labels собирает метки независимо от порядка ячеек")
    void labelsCollectsFromAllCells() {
        GameField f = makeField(2, 2);
        Player p = new Player(f, "X");
        f.setLabel(new Point(2, 2), labelFor(f, p));
        f.setLabel(new Point(1, 1), labelFor(f, p));

        List<Label> labels = f.labels();
        assertEquals(2, labels.size());
        assertTrue(labels.stream().allMatch(l -> l.owner().equals(p)));
    }
}
