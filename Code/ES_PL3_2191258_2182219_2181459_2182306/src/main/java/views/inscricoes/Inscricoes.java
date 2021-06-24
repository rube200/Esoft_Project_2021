package views.inscricoes;

import API.DatabaseConnector;
import API.InscricoesController;
import API.ViewBase;
import com.google.inject.Inject;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Date;

public class Inscricoes implements ViewBase {
    private final DefaultListModel<Evento> eventosListModel = new DefaultListModel<>();
    private final DefaultListModel<Prova> provasListModel = new DefaultListModel<>();
    private final DefaultListModel<Atleta> atletasListModel = new DefaultListModel<>();
    private JPanel mainPanel;
    private JList<Evento> listEventos;
    private JList<Prova> listProvas;
    private JList<Atleta> listAtletas;
    private JButton buttonInscrever;
    private JButton buttonVoltar;
    @Inject
    private DatabaseConnector databaseConnector;
    @Inject
    private InscricoesController inscricoesController;

    public Inscricoes() {
        setupButtons();
        setupList();
    }

    @Override
    public Container getViewContainer() {
        return mainPanel;
    }

    @Override
    public boolean prepareView() {
        if (buttonInscrever.isEnabled())
            buttonInscrever.setEnabled(false);

        eventosListModel.clear();
        Collection<Evento> eventos = databaseConnector.getEventoAtuaisOuFuturos();
        if (eventos == null || eventos.isEmpty())
            eventosListModel.addElement(new SemDadosEventos());
        else
            eventosListModel.addAll(eventos);
        listEventos.clearSelection();

        atletasListModel.clear();
        atletasListModel.addElement(new SemDadoAtletas());
        listAtletas.clearSelection();

        provasListModel.clear();
        provasListModel.addElement(new SemDadosProvas());
        listProvas.clearSelection();
        return true;
    }

    @Override
    public void setupBackButton(Runnable buttonBackCallback) {
        buttonVoltar.addActionListener(e -> buttonBackCallback.run());
    }

    private void setupButtons() {
        buttonInscrever.addActionListener(e -> {
            Prova prova = listProvas.getSelectedValue();
            if (prova == null || prova.getId() < 1)
                return;

            inscricoesController.mostrarInscreverEmProvaAtleta(prova);
        });
    }

    private void setupList() {
        listEventos.setModel(eventosListModel);
        listEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listProvas.setModel(provasListModel);
        listProvas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listAtletas.setModel(atletasListModel);
        listAtletas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listEventos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                return;

            Evento evento = listEventos.getSelectedValue();
            if (evento == null || evento.getId() < 1)
                return;

            if (buttonInscrever.isEnabled())
                buttonInscrever.setEnabled(false);

            atletasListModel.clear();
            atletasListModel.addElement(new SemDadoAtletas());
            listAtletas.clearSelection();

            provasListModel.clear();
            Collection<Prova> provas = databaseConnector.getProvas(evento);
            if (provas == null || provas.isEmpty())
                provasListModel.addElement(new SemDadosProvas());
            else
                provasListModel.addAll(provas);
            listProvas.clearSelection();
        });

        listProvas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                return;

            Prova prova = listProvas.getSelectedValue();
            if (prova == null || prova.getId() < 1)
                return;

            if (prova.getDataDaProvaTime() >= new Date().getTime()) {
                if (!buttonInscrever.isEnabled())
                    buttonInscrever.setEnabled(true);
            } else {
                if (buttonInscrever.isEnabled())
                    buttonInscrever.setEnabled(false);
            }

            atletasListModel.clear();
            Collection<Atleta> atletas = databaseConnector.getAtletasInscritos(prova);
            if (atletas == null || atletas.isEmpty())
                atletasListModel.addElement(new SemDadoAtletas());
            else
                atletasListModel.addAll(atletas);
            listAtletas.clearSelection();
        });
    }
}
