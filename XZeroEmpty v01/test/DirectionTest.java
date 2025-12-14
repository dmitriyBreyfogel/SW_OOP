import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xzero.model.navigation.Direction;
import xzero.model.navigation.Shift;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Direction: смещения, вращение и противоположность")
class DirectionTest {

    @Test
    @DisplayName("Тест №1: север даёт вертикальное смещение вверх")
    void northShiftIsUpward() {
        Shift shift = Direction.north().shift();
        assertEquals(0, shift.byHorizontal());
        assertEquals(-1, shift.byVertical());
    }

    @Test
    @DisplayName("Тест №2: юг даёт вертикальное смещение вниз")
    void southShiftIsDownward() {
        Shift shift = Direction.south().shift();
        assertEquals(0, shift.byHorizontal());
        assertEquals(1, shift.byVertical());
    }

    @Test
    @DisplayName("Тест №3: восток сдвигает по горизонтали вправо")
    void eastShiftIsRightward() {
        Shift shift = Direction.east().shift();
        assertEquals(1, shift.byHorizontal());
        assertEquals(0, shift.byVertical());
    }

    @Test
    @DisplayName("Тест №4: запад сдвигает по горизонтали влево")
    void westShiftIsLeftward() {
        Shift shift = Direction.west().shift();
        assertEquals(-1, shift.byHorizontal());
        assertEquals(0, shift.byVertical());
    }

    @Test
    @DisplayName("Тест №5: северо-восток комбинирует оба смещения")
    void northEastCombinesShifts() {
        Shift shift = Direction.northEast().shift();
        assertEquals(1, shift.byHorizontal());
        assertEquals(-1, shift.byVertical());
    }

    @Test
    @DisplayName("Тест №6: opposite возвращает противоположное направление")
    void oppositeDirectionMatchesReverse() {
        Direction east = Direction.east();
        Direction west = Direction.west();
        assertTrue(east.isOpposite(west));
        assertTrue(west.isOpposite(east));
    }

    @Test
    @DisplayName("Тест №7: clockwise вращает на 45 градусов")
    void clockwiseRotatesByStep() {
        Direction start = Direction.north();
        Direction rotated = start.clockwise();
        assertEquals(Direction.northEast(), rotated);
    }

    @Test
    @DisplayName("Тест №8: anticlockwise вращает на 45 градусов против часовой")
    void anticlockwiseRotatesByStep() {
        Direction start = Direction.east();
        Direction rotated = start.anticlockwise();
        assertEquals(Direction.northEast(), rotated);
    }

    @Test
    @DisplayName("Тест №9: rightword эквивалентен clockwise")
    void rightwordEqualsClockwise() {
        Direction base = Direction.south();
        assertEquals(base.clockwise(), base.rightword());
    }

    @Test
    @DisplayName("Тест №10: leftword эквивалентен anticlockwise")
    void leftwordEqualsAnticlockwise() {
        Direction base = Direction.southEast();
        assertEquals(base.anticlockwise(), base.leftword());
    }
}
