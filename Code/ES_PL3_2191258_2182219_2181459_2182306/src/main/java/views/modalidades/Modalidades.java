package views.modalidades;

import API.CrudController;
import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import model.Modalidade;
import views.model.ModelCrud;
import views.model.ModelListRender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

public class Modalidades extends JFrame implements ViewBase {
    private final DefaultListModel<ModelCrud<Modalidade>> modalidadesListModel = new DefaultListModel<>();
    private JPanel mainPanel;
    private JList<ModelCrud<Modalidade>> listModalidades;
    private JButton buttonVoltar;
    private JButton buttonNovaModalidade;
    private JButton btn_hist;
    private JButton btn_detalhes;

    @Inject
    @Named("ModalidadesController")
    private CrudController<Modalidade> modalidadesController;
    @Inject
    private DatabaseConnector databaseConnector;

    public Modalidades() {
        setupButtons();
        setupList();
    }

    @Override
    public Container getViewContainer() {
        return mainPanel;
    }

    @Override
    public boolean prepareView() {
        Collection<Modalidade> modalidades = databaseConnector.getModalidades();
        if (modalidades == null)
            return false;
        modalidadesListModel.clear();
        for (Modalidade modalidade : modalidades) {
            ModelCrud<Modalidade> listRow = new ModelCrud<>(modalidade, () -> modalidadesController.edit(modalidade), () -> modalidadesController.destroy(modalidade));
            modalidadesListModel.addElement(listRow);
        }

        return true;
    }

    @Override
    public void setupBackButton(Runnable buttonBackCallback) {
        buttonVoltar.addActionListener(e -> buttonBackCallback.run());
    }

    private void setupButtons() {
        buttonNovaModalidade.addActionListener(e -> modalidadesController.create());
    }

    private void setupList() {
        listModalidades.setCellRenderer(new ModelListRender<>());
        listModalidades.setModel(modalidadesListModel);
        listModalidades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listModalidades.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = listModalidades.locationToIndex(e.getPoint());
                ModelCrud<Modalidade> model = listModalidades.getModel().getElementAt(index);
                if (model == null)
                    return;

                model.onModelPress(e.getX());
            }
        });
    }
}
