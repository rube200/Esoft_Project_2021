package views.modalidades;

import API.CrudController;
import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import model.Modalidade;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class Modalidades extends JFrame implements ViewBase {
    private JPanel mainPanel;

    private JList<Modalidade> listModalidades;
    private final DefaultListModel<Modalidade> modalidadesListModel = new DefaultListModel<>();

    private JButton buttonVoltar;
    private JButton buttonNovaModalidade;
    private JButton btn_hist;
    private JButton btn_detalhes;

    @Inject
    @Named("ModalidadesController")
    private CrudController<Modalidade> modalidadesController;

    public Modalidades(){
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
        Collection<Modalidade> modalidades = databaseConnector.getModalidades();
        if (modalidades == null)
            return false;
        modalidadesListModel.clear();
        modalidadesListModel.addAll(modalidades);

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
        listModalidades.setModel(modalidadesListModel);
        listModalidades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}
