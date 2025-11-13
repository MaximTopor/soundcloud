package org.example;

import org.example.ui.MainWindow;

import javax.swing.*;

public class main
{
    public static void main(String[] args) {
        startGUI();
    }


    private static void startGUI() {
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}