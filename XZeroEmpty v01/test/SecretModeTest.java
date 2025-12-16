import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import xzero.model.GameModel;
import xzero.model.Player;
import xzero.model.labels.Label;
import xzero.model.labels.LabelType;
import xzero.model.labels.SecretLabel;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GameModel: секретный режим скрывает вид до установки")
class SecretModeTest {

    private GameModel model;

    @BeforeEach
    void setup() {
        model = new GameModel();
        model.setSecretModeEnabled(true);
        model.start();
    }

    /**
     * Проверяет, что секретная метка раскрывается в нужный тип и не остаётся скрытой.
     *
     * @param col столбец
     * @param row строка
     * @param type скрываемый тип
     */
    private void assertSecretPlacement(int col, int row, LabelType type) {
        model.setActiveLabelType(type);
        Player current = model.activePlayer();

        Label secret = current.activeLabel();
        assertTrue(secret instanceof SecretLabel);
        assertEquals("*", secret.symbol());

        String expectedSymbol = switch (type) {
            case HIDDEN -> "?";
            case NORMAL -> current.name();
            case DELEGATED -> current.name().equals("X") ? "O" : "X";
        };

        Point pos = new Point(col, row);
        current.setLabelTo(pos);

        Label placed = model.field().label(pos);
        assertNotNull(placed);
        assertFalse(placed instanceof SecretLabel);
        assertEquals(expectedSymbol, placed.symbol());
    }

    @Test
    void secret_normal_1_1() {
        assertSecretPlacement(1, 1, LabelType.NORMAL);
    }

    @Test
    void secret_normal_1_2() {
        assertSecretPlacement(2, 1, LabelType.NORMAL);
    }

    @Test
    void secret_normal_1_3() {
        assertSecretPlacement(3, 1, LabelType.NORMAL);
    }

    @Test
    void secret_normal_1_4() {
        assertSecretPlacement(4, 1, LabelType.NORMAL);
    }

    @Test
    void secret_normal_1_5() {
        assertSecretPlacement(5, 1, LabelType.NORMAL);
    }

    @Test
    void secret_normal_2_1() {
        assertSecretPlacement(1, 2, LabelType.NORMAL);
    }

    @Test
    void secret_normal_2_2() {
        assertSecretPlacement(2, 2, LabelType.NORMAL);
    }

    @Test
    void secret_normal_2_3() {
        assertSecretPlacement(3, 2, LabelType.NORMAL);
    }

    @Test
    void secret_normal_2_4() {
        assertSecretPlacement(4, 2, LabelType.NORMAL);
    }

    @Test
    void secret_normal_2_5() {
        assertSecretPlacement(5, 2, LabelType.NORMAL);
    }

    @Test
    void secret_normal_3_1() {
        assertSecretPlacement(1, 3, LabelType.NORMAL);
    }

    @Test
    void secret_normal_3_2() {
        assertSecretPlacement(2, 3, LabelType.NORMAL);
    }

    @Test
    void secret_normal_3_3() {
        assertSecretPlacement(3, 3, LabelType.NORMAL);
    }

    @Test
    void secret_normal_3_4() {
        assertSecretPlacement(4, 3, LabelType.NORMAL);
    }

    @Test
    void secret_normal_3_5() {
        assertSecretPlacement(5, 3, LabelType.NORMAL);
    }

    @Test
    void secret_normal_4_1() {
        assertSecretPlacement(1, 4, LabelType.NORMAL);
    }

    @Test
    void secret_normal_4_2() {
        assertSecretPlacement(2, 4, LabelType.NORMAL);
    }

    @Test
    void secret_normal_4_3() {
        assertSecretPlacement(3, 4, LabelType.NORMAL);
    }

    @Test
    void secret_normal_4_4() {
        assertSecretPlacement(4, 4, LabelType.NORMAL);
    }

    @Test
    void secret_normal_4_5() {
        assertSecretPlacement(5, 4, LabelType.NORMAL);
    }

    @Test
    void secret_normal_5_1() {
        assertSecretPlacement(1, 5, LabelType.NORMAL);
    }

    @Test
    void secret_normal_5_2() {
        assertSecretPlacement(2, 5, LabelType.NORMAL);
    }

    @Test
    void secret_normal_5_3() {
        assertSecretPlacement(3, 5, LabelType.NORMAL);
    }

    @Test
    void secret_normal_5_4() {
        assertSecretPlacement(4, 5, LabelType.NORMAL);
    }

    @Test
    void secret_normal_5_5() {
        assertSecretPlacement(5, 5, LabelType.NORMAL);
    }

    @Test
    void secret_delegated_1_1() {
        assertSecretPlacement(1, 1, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_1_2() {
        assertSecretPlacement(2, 1, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_1_3() {
        assertSecretPlacement(3, 1, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_1_4() {
        assertSecretPlacement(4, 1, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_1_5() {
        assertSecretPlacement(5, 1, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_2_1() {
        assertSecretPlacement(1, 2, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_2_2() {
        assertSecretPlacement(2, 2, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_2_3() {
        assertSecretPlacement(3, 2, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_2_4() {
        assertSecretPlacement(4, 2, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_2_5() {
        assertSecretPlacement(5, 2, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_3_1() {
        assertSecretPlacement(1, 3, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_3_2() {
        assertSecretPlacement(2, 3, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_3_3() {
        assertSecretPlacement(3, 3, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_3_4() {
        assertSecretPlacement(4, 3, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_3_5() {
        assertSecretPlacement(5, 3, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_4_1() {
        assertSecretPlacement(1, 4, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_4_2() {
        assertSecretPlacement(2, 4, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_4_3() {
        assertSecretPlacement(3, 4, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_4_4() {
        assertSecretPlacement(4, 4, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_4_5() {
        assertSecretPlacement(5, 4, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_5_1() {
        assertSecretPlacement(1, 5, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_5_2() {
        assertSecretPlacement(2, 5, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_5_3() {
        assertSecretPlacement(3, 5, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_5_4() {
        assertSecretPlacement(4, 5, LabelType.DELEGATED);
    }

    @Test
    void secret_delegated_5_5() {
        assertSecretPlacement(5, 5, LabelType.DELEGATED);
    }

    @Test
    void secret_hidden_1_1() {
        assertSecretPlacement(1, 1, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_1_2() {
        assertSecretPlacement(2, 1, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_1_3() {
        assertSecretPlacement(3, 1, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_1_4() {
        assertSecretPlacement(4, 1, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_1_5() {
        assertSecretPlacement(5, 1, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_2_1() {
        assertSecretPlacement(1, 2, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_2_2() {
        assertSecretPlacement(2, 2, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_2_3() {
        assertSecretPlacement(3, 2, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_2_4() {
        assertSecretPlacement(4, 2, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_2_5() {
        assertSecretPlacement(5, 2, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_3_1() {
        assertSecretPlacement(1, 3, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_3_2() {
        assertSecretPlacement(2, 3, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_3_3() {
        assertSecretPlacement(3, 3, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_3_4() {
        assertSecretPlacement(4, 3, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_3_5() {
        assertSecretPlacement(5, 3, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_4_1() {
        assertSecretPlacement(1, 4, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_4_2() {
        assertSecretPlacement(2, 4, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_4_3() {
        assertSecretPlacement(3, 4, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_4_4() {
        assertSecretPlacement(4, 4, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_4_5() {
        assertSecretPlacement(5, 4, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_5_1() {
        assertSecretPlacement(1, 5, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_5_2() {
        assertSecretPlacement(2, 5, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_5_3() {
        assertSecretPlacement(3, 5, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_5_4() {
        assertSecretPlacement(4, 5, LabelType.HIDDEN);
    }

    @Test
    void secret_hidden_5_5() {
        assertSecretPlacement(5, 5, LabelType.HIDDEN);
    }
}
