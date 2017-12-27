package pl.klolo.pomodoro.component.progress;

public class TrayManager {
    /**
     *      if (SystemTray.isSupported()) {
     final URL resource = getClass().getClassLoader().getResource("img/sand-clock.png");
     java.awt.Image image = Toolkit.getDefaultToolkit().getImage(resource);
     SystemTray tray = SystemTray.getSystemTray();
     // creating a popup-menu
     PopupMenu popupMenu = new PopupMenu();
     // creating menu item for the default action
     MenuItem menuItem = new MenuItem();
     Label defaultItem = new Label("sdsd");
     // defaultItem.setOnMouseClicked(actionListener);
     //popup.add(defaultItem);
     /// ... add others item
     TrayIcon trayicon = new TrayIcon(image, "System Tray Demo", popupMenu);
     // set the Tray Icon
     //   trayicon.addActionListener(listener);
     try {
     tray.add(trayicon);
     }
     catch (AWTException exception) {
     System.err.println(exception);
     }
     }
     */
}
