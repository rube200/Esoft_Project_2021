package views.atletas;

import API.CrudController;
import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import model.Atleta;
import views.model.ModelCrud;
import views.model.ModelListRender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

public class Atletas implements ViewBase {
    private final DefaultListModel<ModelCrud<Atleta>> atletasListModel = new DefaultListModel<>();
    private JPanel mainPanel;
    private JList<ModelCrud<Atleta>> listAtletas;
    private JButton buttonNovoAtleta;
    private JButton buttonVoltar;

    @Inject
    @Named("AtletasController")
    private CrudController<Atleta> atletasController;
    @Inject
    private DatabaseConnector databaseConnector;

    public Atletas(){
        setupButtons();
        setupList();
    }

    @Override
    public Container getViewContainer() {
        return mainPanel;
    }

    @Override
    public boolean prepareView() {
        Collection<Atleta> atletas = databaseConnector.getAtletas();
        if (atletas == null)
            return false;
        atletasListModel.clear();
        for (Atleta atleta : atletas) {
            ModelCrud<Atleta> listRow = new ModelCrud<>(atleta, () -> atletasController.edit(atleta), () -> atletasController.destroy(atleta));
            atletasListModel.addElement(listRow);
        }

        return true;
    }

    @Override
    public void setupBackButton(Runnable buttonBackCallback) {
        buttonVoltar.addActionListener(e -> buttonBackCallback.run());
    }

    private void setupButtons() {
        buttonNovoAtleta.addActionListener(e -> atletasController.create());
    }

    private void setupList() {
        listAtletas.setCellRenderer(new ModelListRender<>());
        listAtletas.setModel(atletasListModel);
        listAtletas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listAtletas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = listAtletas.locationToIndex(e.getPoint());
                ModelCrud<Atleta> model = listAtletas.getModel().getElementAt(index);
                if (model == null)
                    return;

                model.onModelPress(e.getX(), e.getY());
            }
        });
    }
}
