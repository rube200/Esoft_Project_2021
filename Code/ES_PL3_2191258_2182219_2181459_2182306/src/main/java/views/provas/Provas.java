package views.provas;

import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import model.Prova;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class Provas extends JFrame implements ViewBase {
    private final DefaultListModel<Prova> provasListModel = new DefaultListModel<>();
    private JPanel mainPanel;
    private JList<Prova> listProvas;
    private JButton buttonNovaProva;
    private JButton buttonInscreverAtleta;
    private JButton buttonDetalhesProva;
    private JButton buttonImportarProvas;
    private JButton buttonVoltar;
    @Inject
    private DatabaseConnector databaseConnector;

    public Provas() {
        //todo
        //setupButtons();
        setupList();
    }

    @Override
    public Container getViewContainer() {
        return mainPanel;
    }

    @Override
    public boolean prepareView() {
        Collection<Prova> provas = databaseConnector.getProvas();
        if (provas == null)
            return false;
        provasListModel.clear();
        provasListModel.addAll(provas);

        return true;
    }

    @Override
    public void setupBackButton(Runnable buttonBackCallback) {
        buttonVoltar.addActionListener(e -> buttonBackCallback.run());
    }

    private void setupList() {
        listProvas.setModel(provasListModel);
        listProvas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}
