import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xzero.model.Cell;
import xzero.model.GameField;
import xzero.model.labels.Label;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cell: хранение метки, ограничения и двусторонние связи")
class CellTest {

    private Cell makeCell(GameField f, int x, int y) {
        Cell c = new Cell();
        c.setField(f);
        c.setPosition(new Point(x,y));
        return c;
    }

    @Test @DisplayName("Тест №1: пустая ячейка пуста, после помещения — не пуста")
    void placeMakesNotEmpty() {
        GameField f = new GameField();
        Cell c = makeCell(f,1,1);
        Label l = new Label();
        assertTrue(c.isEmpty());
        c.placeLabel(l);
        assertFalse(c.isEmpty());
        assertEquals(c, l.cell());
        assertEquals(l, c.label());
    }

    @Test @DisplayName("Тест №2: нельзя поместить null-метку")
    void cannotPlaceNull() {
        GameField f = new GameField();
        Cell c = makeCell(f,1,1);
        assertThrows(IllegalArgumentException.class, () -> c.placeLabel(null));
    }

    @Test @DisplayName("Тест №3: нельзя поместить в занятую ячейку")
    void cannotPlaceIntoBusyCell() {
        GameField f = new GameField();
        Cell c = makeCell(f,1,1);
        c.placeLabel(new Label());
        assertThrows(IllegalStateException.class, () -> c.placeLabel(new Label()));
    }

    @Test @DisplayName("Тест №4: одна метка не может лежать в двух ячейках")
    void oneLabelNotInTwoCells() {
        GameField f = new GameField();
        Cell c1 = makeCell(f,1,1);
        Cell c2 = makeCell(f,2,1);
        Label l = new Label();
        c1.placeLabel(l);
        assertThrows(IllegalStateException.class, () -> c2.placeLabel(l));
    }

    @Test @DisplayName("Тест №5: removeLabel разрывает связь двусторонне")
    void removeLabelBreaksBothSides() {
        GameField f = new GameField();
        Cell c = makeCell(f,1,1);
        Label l = new Label();
        c.placeLabel(l);
        c.removeLabel();
        assertTrue(c.isEmpty());
        assertNull(l.cell());
    }

    @Test @DisplayName("Тест №6: position возвращается копией, а не по ссылке")
    void positionIsDefensiveCopy() {
        GameField f = new GameField();
        Cell c = makeCell(f,2,2);
        Point p = c.position();
        p.translate(5,5);
        assertEquals(new Point(2,2), c.position());
    }

    @Test @DisplayName("Тест №7: повторное removeLabel безопасно")
    void doubleRemoveSafe() {
        GameField f = new GameField();
        Cell c = makeCell(f,1,1);
        c.removeLabel();
        assertTrue(c.isEmpty());
    }

    @Test @DisplayName("Тест №8: метка в ячейке всегда указывает на эту ячейку")
    void labelAlwaysPointsToOwnerCell() {
        GameField f = new GameField();
        Cell c = makeCell(f,1,1);
        Label l = new Label();
        c.placeLabel(l);
        assertSame(c, l.cell());
    }
}
