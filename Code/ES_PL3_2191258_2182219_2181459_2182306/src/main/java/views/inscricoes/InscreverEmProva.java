package views.inscricoes;

import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import model.Atleta;
import model.Prova;
import model.SemDadoAtletas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.*;
import java.util.function.BiConsumer;

public class InscreverEmProva implements ViewBase {
    private final DefaultListModel<Atleta> atletasNaoInscritosListModel = new DefaultListModel<>();
    private final DefaultListModel<Atleta> atletasInscritosListModel = new DefaultListModel<>();
    private final Collection<Atleta> paraInscrever = new HashSet<>();
    private final Collection<Atleta> paraDesinscrever = new HashSet<>();

    private JPanel mainPanel;
    private JLabel labelName;
    private JList<Atleta> listAtletasNaoInscritos;
    private JList<Atleta> listAtletasInscritos;
    private JButton buttonAdicionar;
    private JButton buttonRemover;
    private JButton buttonConcluir;
    private JButton buttonVoltar;
    private Prova prova;
    @Inject
    private DatabaseConnector databaseConnector;
    private Collection<Atleta> inscritos;
    private Collection<Atleta> naoInscritos;

    public InscreverEmProva() {
        setupButtons();
        setupList();
    }

    public void setProva(Prova prova) {
        this.prova = prova;
    }

    @Override
    public Container getViewContainer() {
        return mainPanel;
    }

    @Override
    public boolean prepareView() {
        labelName.setText(prova.toString());

        buttonAdicionar.setEnabled(false);
        buttonRemover.setEnabled(false);

        paraInscrever.clear();
        paraDesinscrever.clear();

        atletasInscritosListModel.clear();
        inscritos = databaseConnector.getAtletasInscritos(prova);
        if (inscritos == null || inscritos.isEmpty())
            atletasInscritosListModel.addElement(new SemDadoAtletas());
        else
            atletasInscritosListModel.addAll(inscritos);
        listAtletasInscritos.clearSelection();

        atletasNaoInscritosListModel.clear();
        naoInscritos = databaseConnector.getAtletasNaoInscritos(prova);
        if (inscritos == null || naoInscritos.isEmpty())
            atletasNaoInscritosListModel.addElement(new SemDadoAtletas());
        else
            atletasNaoInscritosListModel.addAll(naoInscritos);
        listAtletasNaoInscritos.clearSelection();

        return true;
    }

    @Override
    public void setupBackButton(Runnable buttonBackCallback) {
        buttonVoltar.addActionListener(e -> buttonBackCallback.run());
    }

    private void setupButtons() {
        buttonAdicionar.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Atleta> toChangeList = listAtletasNaoInscritos.getSelectedValuesList();
                toggleInscricao(toChangeList, true);
                listAtletasNaoInscritos.clearSelection();
            }
        });
        buttonRemover.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Atleta> toChangeList = listAtletasInscritos.getSelectedValuesList();
                toggleInscricao(toChangeList, false);
                listAtletasInscritos.clearSelection();
            }
        });
    }

    public void setupConcluirButton(BiConsumer<Prova, Map.Entry<List<Atleta>, List<Atleta>>> buttonConcluirCallback) {
        buttonConcluir.addActionListener(e -> buttonConcluirCallback.accept(prova, new AbstractMap.SimpleEntry<>(new ArrayList<>(paraInscrever), new ArrayList<>(paraDesinscrever))));
    }

    private void toggleInscricao(List<Atleta> toChangeList, boolean inscrever) {
        if (toChangeList == null || toChangeList.isEmpty())
            return;

        Atleta firstAtleta = toChangeList.get(0);
        if (firstAtleta.getId() < 1)
            return;

        DefaultListModel<Atleta> toAddModel = inscrever ? atletasInscritosListModel : atletasNaoInscritosListModel;
        if (!toAddModel.isEmpty() && toAddModel.firstElement().getId() < 1) {
            toAddModel.removeElementAt(0);
        }

        DefaultListModel<Atleta> toRemoveModel = inscrever ? atletasNaoInscritosListModel : atletasInscritosListModel;
        for (Atleta atleta : toChangeList) {
            if (inscrever)
                changeList(atleta, toRemoveModel, toAddModel, inscritos, paraInscrever);
            else
                changeList(atleta, toRemoveModel, toAddModel, naoInscritos, paraDesinscrever);
        }

        if (toRemoveModel.isEmpty()) {
            toRemoveModel.addElement(new SemDadoAtletas());
        }
    }

    private void changeList(Atleta atleta, DefaultListModel<Atleta> toRemoveModel, DefaultListModel<Atleta> toAddModel, Collection<Atleta> initial, Collection<Atleta> target) {
        toRemoveModel.removeElement(atleta);
        toAddModel.addElement(atleta);

        if (initial.contains(atleta) || target.contains(atleta))
            return;

        target.add(atleta);
    }

    private void setupList() {
        listAtletasNaoInscritos.setModel(atletasNaoInscritosListModel);
        listAtletasNaoInscritos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listAtletasNaoInscritos.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusLost(e);
                listAtletasInscritos.clearSelection();

                if (!buttonAdicionar.isEnabled())
                    buttonAdicionar.setEnabled(true);

                if (buttonRemover.isEnabled())
                    buttonRemover.setEnabled(false);
            }
        });

        listAtletasInscritos.setModel(atletasInscritosListModel);
        listAtletasInscritos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listAtletasInscritos.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusLost(e);
                listAtletasNaoInscritos.clearSelection();

                if (buttonAdicionar.isEnabled())
                    buttonAdicionar.setEnabled(false);

                if (!buttonRemover.isEnabled())
                    buttonRemover.setEnabled(true);
            }
        });
    }
}
