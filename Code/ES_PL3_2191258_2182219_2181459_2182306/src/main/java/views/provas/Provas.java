package views.provas;

import API.CrudController;
import API.DatabaseConnector;
import API.InscricoesController;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import model.Prova;
import model.SemDadosProvas;
import views.model.ModelCrud;
import views.model.ModelListRender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Date;

public class Provas implements ViewBase {
    private final DefaultListModel<ModelCrud<Prova>> provasListModel = new DefaultListModel<>();
    private JPanel mainPanel;
    private JList<ModelCrud<Prova>> listProvas;
    private JButton buttonNovaProva;
    private JButton buttonInscreverAtleta;
    private JButton buttonDetalhesProva;
    private JButton buttonVoltar;

    @Inject
    private InscricoesController inscricoesController;
    @Inject
    @Named("ProvasController")
    private CrudController<Prova> provasController;
    @Inject
    private DatabaseConnector databaseConnector;

    public Provas() {
        setupButtons();
        setupList();
    }

    @Override
    public Container getViewContainer() {
        return mainPanel;
    }

    @Override
    public boolean prepareView() {
        buttonInscreverAtleta.setEnabled(false);
        buttonDetalhesProva.setEnabled(false);

        provasListModel.clear();
        Collection<Prova> provas = databaseConnector.getProvas();
        if (provas == null || provas.isEmpty())
            provasListModel.addElement(new ModelCrud<>(new SemDadosProvas()));
        else {
            for (Prova prova : provas) {
                ModelCrud<Prova> listRow = new ModelCrud<>(prova, () -> provasController.edit(prova), () -> provasController.destroy(prova));
                provasListModel.addElement(listRow);
            }
        }
        listProvas.clearSelection();

        return true;
    }

    @Override
    public void setupBackButton(Runnable buttonBackCallback) {
        buttonVoltar.addActionListener(e -> buttonBackCallback.run());
    }

    private void setupButtons() {
        buttonNovaProva.addActionListener(e -> provasController.create());
        buttonInscreverAtleta.addActionListener(e -> {
            ModelCrud<Prova> modelCrud = listProvas.getSelectedValue();
            if (modelCrud == null)
                return;

            Prova prova = modelCrud.getModel();
            if (prova.getId() < 1)
                return;

            inscricoesController.mostrarInscreverEmProvaAtleta(prova);
        });
        /* todo create view + call it
        buttonDetalhesProva.addActionListener(e -> {
            ModelCrud<Prova> modelCrud = listProvas.getSelectedValue();
            if (modelCrud == null)
                return;

            Prova prova = modelCrud.getModel();
            if (prova.getId() < 1)
                return;
        });*/
    }

    private void setupList() {
        listProvas.setCellRenderer(new ModelListRender<>());
        listProvas.setModel(provasListModel);
        listProvas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listProvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = listProvas.locationToIndex(e.getPoint());
                ModelCrud<Prova> modelCrud = listProvas.getModel().getElementAt(index);
                if (modelCrud == null)
                    return;

                modelCrud.onModelPress(e.getX(), e.getY());
            }
        });
        listProvas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                return;

            ModelCrud<Prova> modelCrud = listProvas.getSelectedValue();
            if (modelCrud == null)
                return;

            Prova prova = modelCrud.getModel();
            if (prova.getId() < 1) {
                if (buttonInscreverAtleta.isEnabled())
                    buttonInscreverAtleta.setEnabled(false);

                /* later
                if (buttonDetalhesProva.isEnabled())
                    buttonDetalhesProva.setEnabled(false);*/
            } else {
                if (prova.getDataDaProvaTime() >= new Date().getTime()) {
                    if (!buttonInscreverAtleta.isEnabled())
                        buttonInscreverAtleta.setEnabled(true);
                } else {
                    if (buttonInscreverAtleta.isEnabled())
                        buttonInscreverAtleta.setEnabled(false);
                }

                /*  later
                if (!buttonDetalhesProva.isEnabled())
                    buttonDetalhesProva.setEnabled(true);
                    */
            }
        });
    }
}
