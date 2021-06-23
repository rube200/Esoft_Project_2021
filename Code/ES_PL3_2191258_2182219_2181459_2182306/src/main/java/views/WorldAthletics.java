package views;

import API.CrudController;
import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import model.Evento;
import model.Modalidade;
import model.Prova;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class WorldAthletics implements ViewBase {
    private final DefaultListModel<Evento> eventosListModel = new DefaultListModel<>();
    private final DefaultListModel<Prova> provasListModel = new DefaultListModel<>();
    private JPanel mainPanel;
    private JList<Evento> listEventos;//setLayoutOrientation(JList.HORIZONTAL_WRAP);
    private JList<Prova> listProvas;
    private JButton buttonDefinicoes;
    private JButton buttonAtletas;
    private JButton buttonModalidades;
    private JButton buttonInscreverAtleta;
    private JButton buttonMedalhas;
    private JButton buttonTendencias;
    private JButton buttonGerirEventos;
    private JButton buttonGerirProvas;
    private JButton buttonSair;
    @Inject
    private DatabaseConnector databaseConnector;
    @Inject
    @Named("EventosController")
    private CrudController<Evento> eventosController;
    @Inject
    @Named("ModalidadesController")
    private CrudController<Modalidade> modalidadesController;
    @Inject
    @Named("ProvasController")
    private CrudController<Prova> provasController;

    @Inject
    WorldAthletics() {
        setupButtons();
        setupLists();
    }

    @Override
    public Container getViewContainer() {
        return mainPanel;
    }

    @Override
    public boolean prepareView() {
        Collection<Evento> eventos = databaseConnector.getEventosAtuais();
        if (eventos == null)
            return false;
        eventosListModel.clear();
        eventosListModel.addAll(eventos);

        Collection<Prova> provas = databaseConnector.getProvasAtuais();
        if (provas == null)
            return false;
        provasListModel.clear();
        provasListModel.addAll(provas);

        return true;
    }

    @Override
    public void setupBackButton(Runnable buttonBackCallback) {
        buttonSair.addActionListener(e -> buttonBackCallback.run());
    }

    private void setupButtons() {
        buttonGerirEventos.addActionListener(e -> eventosController.index());
        buttonGerirProvas.addActionListener(e -> provasController.index());
        buttonModalidades.addActionListener(e -> modalidadesController.index());
    }

    private void setupLists() {
        listEventos.setModel(eventosListModel);
        listEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listProvas.setModel(provasListModel);
        listProvas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}

