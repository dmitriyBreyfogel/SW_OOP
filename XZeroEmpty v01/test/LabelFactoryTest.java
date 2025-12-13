import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xzero.model.GameField;
import xzero.model.Player;
import xzero.model.factory.LabelFactory;
import xzero.model.labels.Label;
import xzero.model.labels.LabelType;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LabelFactory: выбор владельца, тип и защита от некорректных данных")
class LabelFactoryTest {

    private Player player(String name) {
        return new Player(new GameField(), name);
    }

    @Test
    @DisplayName("Тест №1: фабрика создаёт метку для активного игрока по умолчанию")
    void createsNormalLabelForOwner() {
        LabelFactory factory = new LabelFactory();
        Player owner = player("X");

        Label label = factory.createLabel(owner, LabelType.NORMAL);

        assertEquals(owner, label.owner());
        assertEquals("X", label.symbol());
    }

    @Test
    @DisplayName("Тест №2: делегированная метка принадлежит противнику")
    void delegatedLabelUsesOpponent() {
        LabelFactory factory = new LabelFactory();
        Player current = player("X");
        Player opponent = player("O");

        Label delegated = factory.createLabel(current, opponent, LabelType.DELEGATED);

        assertEquals(opponent, delegated.owner());
        assertEquals("O", delegated.symbol());
    }

    @Test
    @DisplayName("Тест №3: скрытая метка создаётся для активного игрока")
    void hiddenLabelCreatedForCurrentPlayer() {
        LabelFactory factory = new LabelFactory();
        Player current = player("X");

        Label hidden = factory.createLabel(current, player("O"), LabelType.HIDDEN);

        assertEquals(current, hidden.owner());
        assertEquals("?", hidden.symbol());
    }

    @Test
    @DisplayName("Тест №4: попытка создать метку с null-типом вызывает исключение")
    void nullTypeIsRejected() {
        LabelFactory factory = new LabelFactory();
        assertThrows(IllegalArgumentException.class, () -> factory.createLabel(player("X"), null));
    }

    @Test
    @DisplayName("Тест №5: делегированная метка без противника запрещена")
    void delegatedLabelRequiresOpponent() {
        LabelFactory factory = new LabelFactory();
        Player current = player("X");

        assertThrows(IllegalArgumentException.class, () -> factory.createLabel(current, null, LabelType.DELEGATED));
    }

    @Test
    @DisplayName("Тест №6: попытка создать метку без владельца запрещена")
    void ownerIsMandatory() {
        LabelFactory factory = new LabelFactory();
        assertThrows(IllegalArgumentException.class, () -> factory.createLabel(null, LabelType.NORMAL));
    }

    @Test
    @DisplayName("Тест №7: нормальная метка через перегруженный метод принадлежит активному игроку")
    void overloadedMethodUsesCurrentForNormalType() {
        LabelFactory factory = new LabelFactory();
        Player current = player("X");
        Player opponent = player("O");

        Label label = factory.createLabel(current, opponent, LabelType.NORMAL);

        assertEquals(current, label.owner());
        assertEquals("X", label.symbol());
    }

    @Test
    @DisplayName("Тест №8: скрытая метка игнорирует противника и остаётся за текущим игроком")
    void hiddenLabelKeepsCurrentOwnerEvenWithOpponent() {
        LabelFactory factory = new LabelFactory();
        Player current = player("X");
        Player opponent = player("O");

        Label label = factory.createLabel(current, opponent, LabelType.HIDDEN);

        assertEquals(current, label.owner());
        assertEquals("?", label.symbol());
    }
}
