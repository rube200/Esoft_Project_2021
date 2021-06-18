package views;

import API.DatabaseConnector;
import API.MainView;
import com.google.inject.Inject;
import model.Evento;
import model.Prova;

import javax.swing.*;
import java.util.Collection;

public class WorldAthletics extends JFrame implements MainView {
    private final DatabaseConnector databaseConnector;

    private JPanel mainPanel;
    private JList<Evento> listEventos;//setLayoutOrientation(JList.HORIZONTAL_WRAP);
    private final DefaultListModel<Evento> eventosListModel = new DefaultListModel<>();
    private JList<Prova> listProvas;
    private final DefaultListModel<Prova> provasListModel = new DefaultListModel<>();

    private JButton button1;
    private JButton buttonAtletas;
    private JButton buttonModalidades;
    private JButton buttonInscreverAtleta;
    private JButton buttonMedalhas;
    private JButton buttonTendencias;
    private JButton buttonGerirEventos;
    private JButton buttonGerirProvas;
    private JButton buttonSair;

    @Inject
    WorldAthletics(DatabaseConnector databaseConnector) {
        super("World Athletics");
        this.databaseConnector = databaseConnector;

        setContentPane(mainPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        listEventos.setModel(eventosListModel);
        listEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listProvas.setModel(provasListModel);
        listProvas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    @Override
    public void prepareView() {
        Collection<Evento> eventos = databaseConnector.getEventos();
        eventosListModel.addAll(eventos);

        Collection<Prova> provas = databaseConnector.getProvas();
        provasListModel.addAll(provas);
    }

    @Override
    public void displayView() {
        pack();
        this.setVisible(true);
    }
}

