package views;

import API.CrudController;
import API.DatabaseConnector;
import API.InscricoesController;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import controllers.FileIOController;
import controllers.MedalhasController;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class WorldAthletics implements ViewBase {
    private final DefaultListModel<Evento> eventosListModel = new DefaultListModel<>();
    private final DefaultListModel<Prova> provasListModel = new DefaultListModel<>();
    private JPanel mainPanel;
    private JList<Evento> listEventos;
    private JList<Prova> listProvas;
    private JButton buttonDefinicoes;
    private JButton buttonAtletas;
    private JButton buttonModalidades;
    private JButton buttonVerInscricoes;
    private JButton buttonMedalhas;
    private JButton buttonTendencias;
    private JButton buttonGerirEventos;
    private JButton buttonGerirProvas;
    private JButton buttonSair;
    @Inject
    private DatabaseConnector databaseConnector;
    @Inject
    @Named("AtletasController")
    private CrudController<Atleta> atletasController;
    @Inject
    @Named("EventosController")
    private CrudController<Evento> eventosController;
    @Inject
    private FileIOController fileIOController;
    @Inject
    private InscricoesController inscricoesController;
    @Inject
    private MedalhasController medalhasController;
    @Inject
    @Named("ModalidadesController")
    private CrudController<Modalidade> modalidadesController;
    @Inject
    @Named("ProvasController")
    private CrudController<Prova> provasController;

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
        eventosListModel.clear();
        Collection<Evento> eventos = databaseConnector.getEventosAtuais();
        if (eventos == null || eventos.isEmpty())
            eventosListModel.addElement(new SemDadosEventos());
        else
            eventosListModel.addAll(eventos);
        listEventos.clearSelection();

        provasListModel.clear();
        Collection<Prova> provas = databaseConnector.getProvasAtuais();
        if (provas == null || provas.isEmpty())
            provasListModel.addElement(new SemDadosProvas());
        else
            provasListModel.addAll(provas);
        listProvas.clearSelection();

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
        buttonMedalhas.addActionListener(e -> medalhasController.mostrar());
        buttonAtletas.addActionListener(e -> atletasController.index());
        buttonVerInscricoes.addActionListener(e -> inscricoesController.mostrarInscricoes());

        buttonDefinicoes.addActionListener(e -> {
            String setting = JOptionPane.showInputDialog("Introduza os dados sql: ", fileIOController.connectionString());
            fileIOController.saveConnectionString(setting);
        });
    }

    private void setupLists() {
        listEventos.setModel(eventosListModel);
        listEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listProvas.setModel(provasListModel);
        listProvas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}

