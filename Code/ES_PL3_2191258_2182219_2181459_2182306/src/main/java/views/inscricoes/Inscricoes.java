package views.inscricoes;

import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import controllers.InscricoesController;
import model.Atleta;
import model.Evento;
import model.Prova;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class Inscricoes implements ViewBase {
    //todo bloquear inscricao em provas a decorrer
    private final DefaultListModel<Evento> eventosListModel = new DefaultListModel<>();
    private JPanel mainPanel;
    private JList<Evento> listEventos;
    private JList<Prova> listProvas;
    private JList<Atleta> listAtletas;
    private JButton buttonInscrever;
    private JButton buttonVoltar;

    public Inscricoes() {
        setupButtons();
        setupList();
    }

    @Override
    public Container getViewContainer() {
        return mainPanel;
    }

    @Inject
    private DatabaseConnector databaseConnector;

    @Override
    public boolean prepareView() {
        Collection<Evento> eventos = databaseConnector.getEventoAtuaisOuFuturos();
        if (eventos == null)
            return false;
        eventosListModel.clear();
        eventosListModel.addAll(eventos);
        return true;
    }

    @Override
    public void setupBackButton(Runnable buttonBackCallback) {
        buttonVoltar.addActionListener(e -> buttonBackCallback.run());
    }

    @Inject
    private InscricoesController inscricoesController;

    private void setupButtons() {
        buttonInscrever.addActionListener(e -> {
            Prova prova = listProvas.getSelectedValue();
            if (prova == null)
                return;

            inscricoesController.mostrarInscreverAtleta(prova);
        });
    }

    private void setupList() {
        listEventos.setModel(eventosListModel);
        listEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listEventos.addListSelectionListener(e -> {

        });
    }
}
