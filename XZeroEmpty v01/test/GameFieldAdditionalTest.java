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

@DisplayName("GameField (дополнительно): ячейки, линии и корректность связей")
class GameFieldAdditionalTest {

    private GameField filledField(int w, int h) {
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

    private Label label(GameField f, Player owner) {
        return new NormalLabel(owner);
    }

    @Test
    @DisplayName("Тест №1: setCell заменяет ячейку и обновляет её координаты")
    void setCellReplacesAndUpdatesPosition() {
        GameField f = filledField(2, 2);
        Cell replacement = new Cell();
        replacement.setField(f);
        replacement.setPosition(new Point(2, 2));

        f.setCell(new Point(2, 2), replacement);

        Label label = label(f, new Player(f, "X"));
        f.setLabel(new Point(2, 2), label);

        assertSame(replacement, label.cell());
        assertEquals(new Point(2, 2), replacement.position());
    }

    @Test
    @DisplayName("Тест №2: уменьшение поля сохраняет метки в допустимых пределах")
    void shrinkKeepsLabelsInsideRange() {
        GameField f = filledField(3, 3);
        Label l = label(f, new Player(f, "X"));
        f.setLabel(new Point(1, 1), l);

        f.setSize(2, 2);

        assertEquals(l, f.label(new Point(1, 1)));
        assertEquals(1, f.labels().size());
    }

    @Test
    @DisplayName("Тест №3: labelLine корректно обрывается на границе поля")
    void labelLineStopsAtBoundary() {
        GameField f = filledField(3, 3);
        Player p = new Player(f, "X");
        f.setLabel(new Point(3, 1), label(f, p));
        f.setLabel(new Point(3, 2), label(f, p));

        List<Label> line = f.labelLine(new Point(3, 1), Direction.south());

        assertEquals(2, line.size());
        assertTrue(line.stream().allMatch(l -> l.owner().equals(p)));
    }

    @Test
    @DisplayName("Тест №4: labelLine в обратном направлении возвращает ту же длину")
    void labelLineSymmetricInOppositeDirection() {
        GameField f = filledField(4, 1);
        Player p = new Player(f, "O");
        f.setLabel(new Point(1, 1), label(f, p));
        f.setLabel(new Point(2, 1), label(f, p));
        f.setLabel(new Point(3, 1), label(f, p));

        List<Label> forward = f.labelLine(new Point(1, 1), Direction.east());
        List<Label> backward = f.labelLine(new Point(3, 1), Direction.west());

        assertEquals(forward.size(), backward.size());
        assertEquals(3, backward.size());
    }

    @Test
    @DisplayName("Тест №5: labels очищается после clear и не содержит старых ссылок")
    void labelsEmptyAfterClear() {
        GameField f = filledField(2, 2);
        f.setLabel(new Point(1, 1), label(f, new Player(f, "X")));

        f.clear();

        assertTrue(f.labels().isEmpty());
        assertNull(f.label(new Point(1, 1)));
    }

    @Test
    @DisplayName("Тест №6: setLabel требует наличие ячейки даже после повторной установки")
    void setLabelRequiresExistingCellAfterReplacement() {
        GameField f = new GameField();
        f.setSize(1, 1);
        assertThrows(IllegalStateException.class, () -> f.setLabel(new Point(1, 1), label(f, new Player(f, "X"))));
    }
}
