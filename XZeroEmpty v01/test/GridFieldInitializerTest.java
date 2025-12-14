import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xzero.model.Cell;
import xzero.model.GameField;
import xzero.model.Player;
import xzero.model.factory.CellFactory;
import xzero.model.labels.Label;
import xzero.model.labels.NormalLabel;
import xzero.model.setup.GridFieldInitializer;

import java.awt.Point;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GridFieldInitializer: подготовка поля и использование фабрики")
class GridFieldInitializerTest {

    @Test
    @DisplayName("Тест №1: размеры поля должны быть положительными")
    void constructorRejectsNonPositiveSize() {
        assertThrows(IllegalArgumentException.class, () -> new GridFieldInitializer(0, 3));
        assertThrows(IllegalArgumentException.class, () -> new GridFieldInitializer(3, -1));
    }

    @Test
    @DisplayName("Тест №2: prepare устанавливает размер поля и создаёт все ячейки")
    void prepareSetsSizeAndCreatesCells() {
        GameField field = new GameField();
        GridFieldInitializer initializer = new GridFieldInitializer(3, 2);
        initializer.prepare(field, new CellFactory());

        assertEquals(3, field.width());
        assertEquals(2, field.height());

        Player p = new Player(field, "P");
        Label l = new NormalLabel(p);
        assertDoesNotThrow(() -> field.setLabel(new Point(1, 1), l));
    }

    @Test
    @DisplayName("Тест №3: prepare очищает прежние метки и ячейки")
    void prepareClearsPreviousState() {
        GameField field = new GameField();
        field.setSize(1, 1);
        Cell cell = new Cell();
        cell.setField(field);
        cell.setPosition(new Point(1, 1));
        field.setCell(new Point(1, 1), cell);
        field.setLabel(new Point(1, 1), new NormalLabel(new Player(field, "X")));
        assertFalse(field.labels().isEmpty());

        GridFieldInitializer initializer = new GridFieldInitializer(2, 2);
        initializer.prepare(field, new CellFactory());

        assertTrue(field.labels().isEmpty());
        assertEquals(2, field.width());
        assertEquals(2, field.height());
    }

    @Test
    @DisplayName("Тест №4: фабрика вызывается для каждой создаваемой ячейки")
    void factoryUsedForEachCell() {
        GameField field = new GameField();
        AtomicInteger counter = new AtomicInteger();
        CellFactory countingFactory = new CellFactory() {
            @Override
            public Cell createCell() {
                counter.incrementAndGet();
                return super.createCell();
            }
        };

        GridFieldInitializer initializer = new GridFieldInitializer(2, 3);
        initializer.prepare(field, countingFactory);

        assertEquals(6, counter.get());
    }

    @Test
    @DisplayName("Тест №5: повторная подготовка изменяет размер поля")
    void prepareOverridesPreviousSize() {
        GameField field = new GameField();
        GridFieldInitializer initializer = new GridFieldInitializer(4, 1);
        initializer.prepare(field, new CellFactory());

        GridFieldInitializer newInitializer = new GridFieldInitializer(1, 2);
        newInitializer.prepare(field, new CellFactory());

        assertEquals(1, field.width());
        assertEquals(2, field.height());
    }

    @Test
    @DisplayName("Тест №6: созданные ячейки получают ссылку на поле и позицию")
    void createdCellsHaveFieldAndPosition() {
        GameField field = new GameField();
        GridFieldInitializer initializer = new GridFieldInitializer(2, 2);
        initializer.prepare(field, new CellFactory());

        Player p = new Player(field, "P");
        Label l = new NormalLabel(p);
        field.setLabel(new Point(2, 2), l);

        assertEquals(new Point(2, 2), l.cell().position());
        assertSame(l, field.label(new Point(2, 2)));
    }

    @Test
    @DisplayName("Тест №7: подготовленное поле принимает метки во все ячейки")
    void preparedFieldAcceptsLabelsEverywhere() {
        GameField field = new GameField();
        GridFieldInitializer initializer = new GridFieldInitializer(2, 2);
        initializer.prepare(field, new CellFactory());

        Player p = new Player(field, "P");
        for (int y = 1; y <= field.height(); y++) {
            for (int x = 1; x <= field.width(); x++) {
                assertDoesNotThrow(() -> field.setLabel(new Point(x, y), new NormalLabel(p)));
            }
        }
    }

    @Test
    @DisplayName("Тест №8: повторный вызов prepare очищает предыдущие метки")
    void repeatedPrepareClearsOldLabels() {
        GameField field = new GameField();
        GridFieldInitializer initializer = new GridFieldInitializer(2, 2);
        initializer.prepare(field, new CellFactory());

        Player p = new Player(field, "P");
        field.setLabel(new Point(1, 1), new NormalLabel(p));
        assertFalse(field.labels().isEmpty());

        initializer.prepare(field, new CellFactory());
        assertTrue(field.labels().isEmpty());
    }
}
