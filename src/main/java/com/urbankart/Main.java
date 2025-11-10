package com.urbankart;

import com.urbankart.ui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        // Ensure all UI operations are on the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Set system look and feel for a native appearance
            try {
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // If the system L&F fails, it will fall back to the default.
                // Logging the error is good for debugging.
                e.printStackTrace();
            }
            new LoginFrame().setVisible(true);
        });
    }
}
