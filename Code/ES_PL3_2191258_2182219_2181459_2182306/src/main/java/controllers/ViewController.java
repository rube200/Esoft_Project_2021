package controllers;

import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import javax.swing.*;
import java.awt.*;

public class ViewController {
    @Inject
    private JFrame mainWindow;

    @Inject
    @Named("WorldAthleticsView")
    private ViewBase worldAthleticsView;
    private boolean firstDisplay = true;

    public void initiateProgram() {
        worldAthleticsView.setupBackButton(() -> {
            //todo
            mainWindow.dispose();
            System.exit(0);
        });
        displayView(worldAthleticsView);
    }

    void displayView(ViewBase viewBase) {
        if (!viewBase.prepareView()) {
            //todo display error or something
            return;
        }

        Container container = viewBase.getViewContainer();
        mainWindow.setContentPane(container);

        //Every time that content pane change pack need to be called, and every time pack is called the window resize, this is to keep the old size
        if (!firstDisplay) {
            Dimension size = mainWindow.getSize();
            mainWindow.pack();
            mainWindow.setSize(size);
        } else {
            firstDisplay = false;
            mainWindow.pack();
        }

        //This method internal checks if it is already visible
        mainWindow.setVisible(true);
    }

    void displayPopup(JFrame frame) {
        frame.pack();
        frame.setLocationRelativeTo(mainWindow);
        frame.setVisible(true);
    }

    void onBackRequested() {
        displayView(worldAthleticsView);
    }
}
