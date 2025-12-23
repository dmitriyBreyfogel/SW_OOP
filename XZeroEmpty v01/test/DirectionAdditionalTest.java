import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xzero.model.navigation.Direction;
import xzero.model.navigation.Shift;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Direction (дополнительно): цикличность и соответствие equals/hashCode")
class DirectionAdditionalTest {

    @Test
    @DisplayName("Тест №1: восемь поворотов rightword возвращают исходное направление")
    void eightRightTurnsReturnToStart() {
        Direction dir = Direction.north();
        for (int i = 0; i < 8; i++) {
            dir = dir.rightword();
        }
        assertEquals(Direction.north(), dir);
    }

    @Test
    @DisplayName("Тест №2: anticlockwise и clockwise симметричны")
    void clockwiseAndAnticlockwiseAreOppositeOperations() {
        Direction dir = Direction.east();
        Direction rotated = dir.clockwise().anticlockwise();

        assertEquals(dir, rotated);
    }

    @Test
    @DisplayName("Тест №3: hashCode соответствует equals и смещение отражает угол")
    void hashCodeMatchesEqualsAndShift() {
        Direction d1 = Direction.southWest();
        Direction d2 = Direction.south().leftword();

        assertEquals(d1, d2);
        assertEquals(d1.hashCode(), d2.hashCode());

        Shift shift = d1.shift();
        assertEquals(-1, shift.byHorizontal());
        assertEquals(1, shift.byVertical());
    }
}
