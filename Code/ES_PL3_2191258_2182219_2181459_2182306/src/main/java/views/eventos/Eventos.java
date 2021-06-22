package views.eventos;

import API.CrudController;
import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import model.Evento;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class Eventos extends JFrame implements ViewBase {
    private final DefaultListModel<Evento> eventosListModel = new DefaultListModel<>();
    private JPanel mainPanel;
    private JList<Evento> listEventos;
    private JButton buttonNovoEvento;
    private JButton btn_elimi;
    private JButton btn_prog;
    private JButton btn_importar;
    private JButton buttonVoltar;

    @Inject
    @Named("EventosController")
    private CrudController<Evento> eventosController;

    public Eventos() {
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
        Collection<Evento> eventos = databaseConnector.getEventos();
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

    private void setupButtons() {
        buttonNovoEvento.addActionListener(e -> eventosController.create());
    }

    private void setupList() {
        listEventos.setModel(eventosListModel);
        listEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
}
