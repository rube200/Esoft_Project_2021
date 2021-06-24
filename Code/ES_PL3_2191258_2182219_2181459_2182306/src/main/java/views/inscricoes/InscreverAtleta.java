package views.inscricoes;

import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.*;
import java.util.function.BiConsumer;

public class InscreverAtleta implements ViewBase {
    private final DefaultListModel<Evento> eventosListModel = new DefaultListModel<>();
    private final DefaultListModel<Prova> provasListModel = new DefaultListModel<>();
    private final Collection<Prova> paraInscrever = new HashSet<>();
    private final Collection<Prova> paraDesinscrever = new HashSet<>();

    private JPanel mainPanel;
    private JLabel labelName;
    private JList<Evento> listEventos;
    private JList<Prova> listProvas;
    private JButton buttonGuardar;
    private JButton buttonToggleInscricao;
    private JButton buttonVoltar;
    private Atleta atleta;
    @Inject
    private DatabaseConnector databaseConnector;
    private Collection<Prova> inscrito;
    private Collection<Prova> naoInscrito;

    public InscreverAtleta() {
        setupButtons();
        setupList();
    }

    public void setAtleta(Atleta atleta) {
        this.atleta = atleta;
    }

    @Override
    public Container getViewContainer() {
        return mainPanel;
    }

    @Override
    public boolean prepareView() {
        labelName.setText(atleta.toString());

        eventosListModel.clear();
        Collection<Evento> eventos = databaseConnector.getEventoAtuaisOuFuturos();
        if (eventos == null || eventos.isEmpty())
            eventosListModel.addElement(new SemDadosEventos());
        else
            eventosListModel.addAll(eventos);
        listEventos.clearSelection();

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
        buttonToggleInscricao.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Prova prova = listProvas.getSelectedValue();
                if (prova == null || prova.getId() < 1)
                    return;

                toggleInscricao();
                listProvas.clearSelection();
            }
        });
    }

    public void setupConcluirButton(BiConsumer<Atleta, Map.Entry<List<Prova>, List<Prova>>> buttonConcluirCallback) {
        buttonGuardar.addActionListener(e -> buttonConcluirCallback.accept(atleta, new AbstractMap.SimpleEntry<>(new ArrayList<>(paraInscrever), new ArrayList<>(paraDesinscrever))));
    }

    private void toggleInscricao() {
        ProvaInscricao prova = (ProvaInscricao) listProvas.getSelectedValue();
        if (prova == null || prova.getId() < 1)
            return;

        if (prova.inscrito)
            changeList(prova, paraInscrever, paraDesinscrever, naoInscrito);
        else
            changeList(prova, paraDesinscrever, paraInscrever, inscrito);

        prova.inscrito = !prova.inscrito;
    }

    private void changeList(Prova prova, Collection<Prova> toRemove, Collection<Prova> toAdd, Collection<Prova> initial) {
        toRemove.remove(prova);
        if (initial.contains(prova) || toAdd.contains(prova))
            return;

        toAdd.add(prova);
    }

    private void setupList() {
        listEventos.setModel(eventosListModel);
        listEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listEventos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                return;

            Evento evento = listEventos.getSelectedValue();
            if (evento == null || evento.getId() < 1)
                return;

            if (buttonToggleInscricao.isEnabled())
                buttonToggleInscricao.setEnabled(false);

            provasListModel.clear();
            inscrito = databaseConnector.getProvasInscrito(evento, atleta);
            if (inscrito != null && !inscrito.isEmpty()) {
                for (Prova prova : inscrito) {
                    Prova listRow = new ProvaInscricao(true, prova);
                    provasListModel.addElement(listRow);
                }
            }
            naoInscrito = databaseConnector.getProvasNaoInscrito(evento, atleta);
            if (naoInscrito != null && !naoInscrito.isEmpty()) {
                for (Prova prova : naoInscrito) {
                    Prova listRow = new ProvaInscricao(false, prova);
                    provasListModel.addElement(listRow);
                }
            }
            if (provasListModel.isEmpty())
                provasListModel.addElement(new SemDadosProvas());
            listProvas.clearSelection();
        });

        listProvas.setModel(provasListModel);
        listProvas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listProvas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                return;

            Prova prova = listProvas.getSelectedValue();
            if (prova == null || prova.getId() < 1)
                return;

            if (!buttonToggleInscricao.isEnabled())
                buttonToggleInscricao.setEnabled(true);
        });
    }

    public static class ProvaInscricao extends Prova {
        private boolean inscrito;

        private ProvaInscricao(boolean inscrito, Prova prova) {
            super(prova.getId(), prova.getNome());
            this.inscrito = inscrito;
        }

        @Override
        public String toString() {
            return getNome() + " (" + (inscrito ? "Inscrito" : "Por Inscrever") + ")";
        }
    }
}
