import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xzero.model.navigation.Shift;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Shift: хранение и применение смещений")
class ShiftTest {

    @Test
    @DisplayName("Тест №1: значения смещения сохраняются как есть")
    void storesProvidedOffsets() {
        Shift shift = new Shift(2, -3);
        assertEquals(2, shift.byHorizontal());
        assertEquals(-3, shift.byVertical());
    }

    @Test
    @DisplayName("Тест №2: нулевое смещение оставляет точку на месте")
    void zeroShiftKeepsPoint() {
        Shift shift = new Shift(0, 0);
        Point p = new Point(5, 5);
        p.translate(shift.byHorizontal(), shift.byVertical());
        assertEquals(new Point(5, 5), p);
    }

    @Test
    @DisplayName("Тест №3: смещение влияет на обе координаты")
    void shiftAffectsBothCoordinates() {
        Shift shift = new Shift(-1, 4);
        Point p = new Point(0, 0);
        p.translate(shift.byHorizontal(), shift.byVertical());
        assertEquals(new Point(-1, 4), p);
    }

    @Test
    @DisplayName("Тест №4: разные экземпляры с одинаковыми значениями ведут себя одинаково")
    void equalOffsetsBehaveSimilarly() {
        Shift s1 = new Shift(1, 1);
        Shift s2 = new Shift(1, 1);
        Point p1 = new Point(0, 0);
        Point p2 = new Point(0, 0);
        p1.translate(s1.byHorizontal(), s1.byVertical());
        p2.translate(s2.byHorizontal(), s2.byVertical());
        assertEquals(p1, p2);
    }

    @Test
    @DisplayName("Тест №5: смещение может двигать точку в отрицательном направлении")
    void negativeShiftMovesLeftAndUp() {
        Shift shift = new Shift(-2, -2);
        Point p = new Point(3, 3);
        p.translate(shift.byHorizontal(), shift.byVertical());
        assertEquals(new Point(1, 1), p);
    }
}
