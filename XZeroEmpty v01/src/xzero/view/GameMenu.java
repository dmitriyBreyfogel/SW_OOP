package xzero.view;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Меню управления игрой.
 */
public class GameMenu extends JMenuBar {

    public GameMenu(Runnable onNewGame) {
        JMenu fileMenu = new JMenu("File");

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
