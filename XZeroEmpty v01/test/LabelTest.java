import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xzero.model.Cell;
import xzero.model.GameField;
import Label;
import xzero.model.Player;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Label: двусторонние связи и инварианты")
class LabelTest {

    @Test @DisplayName("Тест №1: новая метка нейтральна и нигде не лежит")
    void newLabelIsNeutral() {
        Label l = new Label();
        assertNull(l.player());
        assertNull(l.cell());
    }

    @Test @DisplayName("Тест №2: связь Label-Player — узнать и забыть")
    void playerBindingMutual() {
        Label l = new Label();
        Player p = new Player(new GameField(), "X");
        l.setPlayer(p);
        assertEquals(p, l.player());
        l.unsetPlayer();
        assertNull(l.player());
    }

    @Test @DisplayName("Тест №3: связь Label-Cell — узнать и забыть")
    void cellBindingMutual() {
        Label l = new Label();
        GameField f = new GameField();
        Cell c = new Cell(); c.setField(f); c.setPosition(new java.awt.Point(1,1));
        l.setCell(c);
        assertEquals(c, l.cell());
        l.unsetCell();
        assertNull(l.cell());
    }

    @Test @DisplayName("Тест №4: владелец и ячейка могут существовать одновременно")
    void cellAndPlayerCoexist() {
        GameField f = new GameField();
        Cell c = new Cell(); c.setField(f); c.setPosition(new java.awt.Point(1,1));
        Label l = new Label(); Player p = new Player(f,"X");
        l.setPlayer(p); l.setCell(c);
        assertEquals(p, l.player());
        assertEquals(c, l.cell());
    }

    @Test @DisplayName("Тест №5: повторные unset не приводят к ошибкам")
    void doubleUnsetSafe() {
        Label l = new Label();
        l.unsetCell(); l.unsetCell();
        l.unsetPlayer(); l.unsetPlayer();
        assertNull(l.cell()); assertNull(l.player());
    }

    @Test @DisplayName("Тест №6: смена владельца перезаписывает игрока")
    void reassignPlayerOverwrites() {
        GameField f = new GameField();
        Player p1 = new Player(f,"X"); Player p2 = new Player(f,"O");
        Label l = new Label();
        l.setPlayer(p1);
        l.setPlayer(p2);
        assertEquals(p2, l.player());
    }

    @Test @DisplayName("Тест №7: смена ячейки перезаписывает ссылку")
    void reassignCellOverwrites() {
        GameField f = new GameField();
        Cell c1 = new Cell(); c1.setField(f); c1.setPosition(new java.awt.Point(1,1));
        Cell c2 = new Cell(); c2.setField(f); c2.setPosition(new java.awt.Point(2,1));
        Label l = new Label();
        l.setCell(c1);
        l.setCell(c2);
        assertEquals(c2, l.cell());
    }

    @Test @DisplayName("Тест №8: без владельца и ячейки метка остаётся валидной")
    void labelRemainsValidWithoutLinks() {
        Label l = new Label();
        assertDoesNotThrow(() -> {});
        assertNull(l.player()); assertNull(l.cell());
    }
}
