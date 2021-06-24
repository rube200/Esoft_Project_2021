package views.inscricoes;

import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import model.Atleta;
import model.Prova;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.*;
import java.util.function.BiConsumer;

public class InscreverAtleta implements ViewBase {
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

    public InscreverAtleta() {
        setupButtons();
        setupList();
    }

    private Prova prova;
    public void setProva(Prova prova) {
        this.prova = prova;
    }

    @Override
    public Container getViewContainer() {
        return mainPanel;
    }

    @Inject
    private DatabaseConnector databaseConnector;

    private Collection<Atleta> inscritos;
    private Collection<Atleta> naoInscritos;

    @Override
    public boolean prepareView() {
        labelName.setText(prova.toString());

        paraInscrever.clear();
        paraDesinscrever.clear();

        inscritos = databaseConnector.getAtletasInscritos(prova);
        if (inscritos == null)
            return false;
        atletasInscritosListModel.clear();
        atletasInscritosListModel.addAll(inscritos);

        naoInscritos = databaseConnector.getAtletasNaoInscritos(prova);
        if (naoInscritos == null)
            return false;
        atletasNaoInscritosListModel.clear();
        atletasNaoInscritosListModel.addAll(naoInscritos);

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
                Collection<Atleta> toChangeList = listAtletasNaoInscritos.getSelectedValuesList();
                changeList(toChangeList, true);
            }
        });
        buttonRemover.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Collection<Atleta> toChangeList = listAtletasInscritos.getSelectedValuesList();
                changeList(toChangeList, false);
            }
        });
    }

    public void setupConcluirButton(BiConsumer<Prova, Map.Entry<List<Atleta>, List<Atleta>>> buttonConcluirCallback) {
        buttonConcluir.addActionListener(e -> buttonConcluirCallback.accept(prova, new AbstractMap.SimpleEntry<>(new ArrayList<>(paraInscrever), new ArrayList<>(paraDesinscrever))));
    }

    private void changeList(Collection<Atleta> toChangeList, boolean inscrever) {
        if (toChangeList == null || toChangeList.isEmpty())
            return;

        for (Atleta atleta : toChangeList) {
            (inscrever ? atletasNaoInscritosListModel : atletasInscritosListModel).removeElement(atleta);
            (inscrever ? atletasInscritosListModel : atletasNaoInscritosListModel).addElement(atleta);

            if ((inscrever ? inscritos : naoInscritos).contains(atleta) || (inscrever ? paraInscrever : paraDesinscrever).contains(atleta))
                continue;

            (inscrever ? paraInscrever : paraDesinscrever).add(atleta);
        }
    }

    private void setupList() {
        listAtletasNaoInscritos.setModel(atletasNaoInscritosListModel);
        listAtletasNaoInscritos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listAtletasNaoInscritos.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusLost(e);
                listAtletasInscritos.clearSelection();
            }
        });

        listAtletasInscritos.setModel(atletasInscritosListModel);
        listAtletasInscritos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listAtletasInscritos.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusLost(e);
                listAtletasNaoInscritos.clearSelection();
            }
        });
    }
}
