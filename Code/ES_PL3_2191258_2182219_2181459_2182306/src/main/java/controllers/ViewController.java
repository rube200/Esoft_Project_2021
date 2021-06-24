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
            mainWindow.dispose();
            System.exit(0);
        });
        displayView(worldAthleticsView);
    }

    void displayView(ViewBase viewBase) {
        if (!viewBase.prepareView()) {
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

    void displayPopup(JDialog dialog) {
        dialog.pack();
        dialog.setLocationRelativeTo(mainWindow);
        dialog.setVisible(true);
    }

    public void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(mainWindow, mensagem, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    void onBackRequested() {
        displayView(worldAthleticsView);
    }

    public void mostrarMensagem(String mensagem) {
        JOptionPane.showMessageDialog(mainWindow, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
}
