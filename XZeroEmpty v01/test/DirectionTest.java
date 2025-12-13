import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xzero.model.navigation.Direction;
import xzero.model.navigation.Shift;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Direction и Shift: повороты, противоположность и смещения")
class DirectionTest {

    @Test
    @DisplayName("Тест №1: предопределённые направления дают ожидаемые смещения")
    void predefinedDirectionsProduceCorrectShifts() {
        assertShift(0, 0, Direction.east());
        assertShift(0, -1, Direction.north());
        assertShift(0, 1, Direction.south());
        assertShift(-1, 0, Direction.west());
    }

    @Test
    @DisplayName("Тест №2: диагонали дают комбинированные смещения")
    void diagonalsProduceCombinedShifts() {
        assertShift(1, -1, Direction.northEast());
        assertShift(-1, -1, Direction.northWest());
        assertShift(1, 1, Direction.southEast());
        assertShift(-1, 1, Direction.southWest());
    }

    @Test
    @DisplayName("Тест №3: rightword и leftword поворачивают на 45 градусов")
    void rotationsWorkWithStep() {
        Direction north = Direction.north();
        Direction northEast = north.rightword();
        Direction east = north.rightword().rightword();

        assertEquals(Direction.northEast(), northEast);
        assertEquals(Direction.east(), east);
    }

    @Test
    @DisplayName("Тест №4: opposite возвращает противоположное направление")
    void oppositeDirection() {
        assertTrue(Direction.north().isOpposite(Direction.south()));
        assertTrue(Direction.west().isOpposite(Direction.east()));
    }

    @Test
    @DisplayName("Тест №5: clone создаёт эквивалентное направление")
    void cloneProducesEquivalentDirection() {
        Direction original = Direction.northWest();
        Direction clone = original.clone();
        assertEquals(original, clone);
        assertNotSame(original, clone);
    }

    private void assertShift(int expectedHorizontal, int expectedVertical, Direction direction) {
        Shift shift = direction.shift();
        assertEquals(expectedHorizontal, shift.byHorizontal());
        assertEquals(expectedVertical, shift.byVertical());
    }
}
