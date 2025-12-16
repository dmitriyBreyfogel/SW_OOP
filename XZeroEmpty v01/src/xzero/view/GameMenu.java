package xzero.view;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Меню управления игровым процессом с основными действиями
 */
public class GameMenu extends JMenuBar {

    /**
     * Создаёт меню игры с действиями запуска новой игры и выхода из приложения
     *
     * @param onNewGame обработчик запуска новой игры
     */
    public GameMenu(Runnable onNewGame) {
        JMenu fileMenu = new JMenu("Игра");

        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(e -> onNewGame.run());
        fileMenu.add(newItem);

        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        add(fileMenu);
    }
}
