import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xzero.model.GameField;
import xzero.model.Player;
import xzero.model.labels.DelegatedLabel;
import xzero.model.labels.HiddenLabel;
import xzero.model.labels.Label;
import xzero.model.labels.NormalLabel;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Label: типы меток, владелец, символ и связи")
class LabelTest {

    @Test
    @DisplayName("Тест №1: обычная метка хранит владельца и отображает его имя")
    void normalLabelKeepsOwner() {
        Player owner = new Player(new GameField(), "X");
        Label label = new NormalLabel(owner);

        assertEquals(owner, label.owner());
        assertEquals("X", label.symbol());
    }

    @Test
    @DisplayName("Тест №2: делегированная метка принадлежит указанному игроку")
    void delegatedLabelStoresOwner() {
        Player owner = new Player(new GameField(), "O");
        Label label = new DelegatedLabel(owner);

        assertEquals(owner, label.owner());
        assertEquals("O", label.symbol());
    }

    @Test
    @DisplayName("Тест №3: скрытая метка маскирует владельца символом вопроса")
    void hiddenLabelMasksOwner() {
        Player owner = new Player(new GameField(), "X");
        Label label = new HiddenLabel(owner);

        assertEquals(owner, label.owner());
        assertEquals("?", label.symbol());
    }

    @Test
    @DisplayName("Тест №4: ссылка на ячейку задаётся и очищается вручную")
    void cellBindingIsMutable() {
        Label label = new NormalLabel(new Player(new GameField(), "X"));

        assertNull(label.cell());
        label.setCell(new xzero.model.Cell());
        assertNotNull(label.cell());
        label.unsetCell();
        assertNull(label.cell());
    }

    @Test
    @DisplayName("Тест №5: кто выдал метку, фиксируется в поле placedBy")
    void placedByTracksIssuer() {
        Player giver = new Player(new GameField(), "O");
        Label label = new NormalLabel(new Player(new GameField(), "X"));

        assertNull(label.getPlacedBy());
        label.setPlacedBy(giver);
        assertEquals(giver, label.getPlacedBy());
        label.unsetPlacedBy();
        assertNull(label.getPlacedBy());
    }

    @Test
    @DisplayName("Тест №6: конструкторы меток отклоняют null-владельца")
    void constructorsRejectNullOwner() {
        assertThrows(IllegalArgumentException.class, () -> new NormalLabel(null));
        assertThrows(IllegalArgumentException.class, () -> new DelegatedLabel(null));
        assertThrows(IllegalArgumentException.class, () -> new HiddenLabel(null));
    }

    @Test
    @DisplayName("Тест №7: повторное задание placedBy замещает старое значение и корректно очищается")
    void placedByCanBeReassignedAndCleared() {
        Player first = new Player(new GameField(), "X");
        Player second = new Player(new GameField(), "O");
        Label label = new NormalLabel(first);

        label.setPlacedBy(first);
        label.setPlacedBy(second);
        assertEquals(second, label.getPlacedBy());

        label.unsetPlacedBy();
        assertNull(label.getPlacedBy());
    }
}
