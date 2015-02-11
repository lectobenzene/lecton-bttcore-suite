package example;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
public class ABC {
   private Tree tree;
   protected Shell shell;
   public static void main(String[] args) {
      try {
         ABC window = new ABC();
         window.open();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   public void open() {
      final Display display = Display.getDefault();
      createContents();
      shell.open();
      shell.layout();
      while (!shell.isDisposed()) {
         if (!display.readAndDispatch())
            display.sleep();
      }
   }
   protected void createContents() {
      shell = new Shell();
      shell.setLayout(new FillLayout());
      shell.setSize(300, 200);
      shell.setText("Dynamic Menu Example");
      tree = new Tree(shell, SWT.BORDER);
      final TreeItem item1 = new TreeItem(tree, SWT.NONE);
      item1.setText("First");
      final TreeItem item2 = new TreeItem(tree, SWT.NONE);
      item2.setText("Second");
      final Menu menu = new Menu(tree);
      tree.setMenu(menu);
      menu.addMenuListener(new MenuAdapter() {
         public void menuShown(MenuEvent e) {
            // Get rid of existing menu items
            MenuItem[] items = menu.getItems();
            for (int i = 0; i < items.length; i++) {
               ((MenuItem) items[i]).dispose();
            }
            // Add menu items for current selection
            MenuItem newItem = new MenuItem(menu, SWT.NONE);
            newItem.setText("Menu for " + tree.getSelection()[0].getText());
         }
      });
   }
}