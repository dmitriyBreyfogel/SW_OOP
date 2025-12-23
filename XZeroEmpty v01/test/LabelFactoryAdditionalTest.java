import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xzero.model.GameField;
import xzero.model.Player;
import xzero.model.factory.LabelFactory;
import xzero.model.labels.Label;
import xzero.model.labels.LabelType;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LabelFactory (дополнительно): делегирование, типы и устойчивость")
class LabelFactoryAdditionalTest {

    private Player player(String name) {
        return new Player(new GameField(), name);
    }

    @Test
    @DisplayName("Тест №1: делегированная метка создаётся с владельцем-противником даже при совпадении имени")
    void delegatedLabelUsesExplicitOpponent() {
        LabelFactory factory = new LabelFactory();
        Player current = player("X");
        Player opponent = player("X");

        Label delegated = factory.createLabel(current, opponent, LabelType.DELEGATED);

        assertEquals(opponent, delegated.owner());
        assertEquals("X", delegated.symbol());
    }

    @Test
    @DisplayName("Тест №2: скрытая метка всегда маскирует символ независимо от имени")
    void hiddenLabelAlwaysMasked() {
        LabelFactory factory = new LabelFactory();
        Label label = factory.createLabel(player("LongName"), LabelType.HIDDEN);

        assertEquals("?", label.symbol());
    }

    @Test
    @DisplayName("Тест №3: повторное создание разных типов возвращает разные экземпляры")
    void differentTypesProduceDistinctInstances() {
        LabelFactory factory = new LabelFactory();
        Player owner = player("X");

        Label normal = factory.createLabel(owner, LabelType.NORMAL);
        Label hidden = factory.createLabel(owner, LabelType.HIDDEN);

        assertNotSame(normal, hidden);
        assertEquals(owner, hidden.owner());
    }

    @Test
    @DisplayName("Тест №4: передача null-противника допустима для обычной метки")
    void nullOpponentAllowedForNormalLabel() {
        LabelFactory factory = new LabelFactory();
        Player owner = player("X");

        Label normal = factory.createLabel(owner, null, LabelType.NORMAL);

        assertEquals(owner, normal.owner());
    }
}
