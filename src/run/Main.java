package run;

import controller.MainFrame;

import java.awt.GridLayout;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        try {
            MainFrame gameFrame = new MainFrame();
            gameFrame.init();
        } catch (Exception e) {
            System.err.println("Failed to load the application due to: " + e.getMessage());
        }
    }
}
