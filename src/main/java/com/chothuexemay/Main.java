package com.chothuexemay;

import com.chothuexemay.controller.DataController;
import com.chothuexemay.view.LoginFrame;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            DataController dataController = new DataController();
            new LoginFrame(dataController).setVisible(true);
        });
    }
} 