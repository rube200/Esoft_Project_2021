package views.provas;

import API.CrudController;
import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import controllers.InscricoesController;
import model.Prova;
import views.model.ModelCrud;
import views.model.ModelListRender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

public class Provas implements ViewBase {
    private final DefaultListModel<ModelCrud<Prova>> provasListModel = new DefaultListModel<>();
    private JPanel mainPanel;
    private JList<ModelCrud<Prova>> listProvas;
    private JButton buttonNovaProva;
    private JButton buttonInscreverAtleta;
    private JButton buttonDetalhesProva;
    private JButton buttonImportarProvas;
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

        Collection<Prova> provas = databaseConnector.getProvas();
        if (provas == null)
            return false;
        provasListModel.clear();
        for (Prova prova : provas) {
            ModelCrud<Prova> listRow = new ModelCrud<>(prova, () -> provasController.edit(prova), () -> provasController.destroy(prova));
            provasListModel.addElement(listRow);
        }

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
            if (modelCrud == null) {
                buttonInscreverAtleta.setEnabled(false);
                return;
            }

            Prova prova = modelCrud.getModel();
            inscricoesController.mostrarInscreverAtleta(prova);
        });
        buttonDetalhesProva.addActionListener(e -> {
            ModelCrud<Prova> modelCrud = listProvas.getSelectedValue();
            if (modelCrud == null) {
                buttonDetalhesProva.setEnabled(false);
                return;
            }

            Prova prova = modelCrud.getModel();
            //todo
        });
    }

    private void setupList() {
        listProvas.setCellRenderer(new ModelListRender<>());
        listProvas.setModel(provasListModel);
        listProvas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listProvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = listProvas.locationToIndex(e.getPoint());
                ModelCrud<Prova> model = listProvas.getModel().getElementAt(index);
                if (model == null)
                    return;

                model.onModelPress(e.getX(), e.getY());
            }
        });
        listProvas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                return;

            if (!buttonInscreverAtleta.isEnabled())
                buttonInscreverAtleta.setEnabled(true);

            if (!buttonDetalhesProva.isEnabled())
                buttonDetalhesProva.setEnabled(true);
        });
    }
}
