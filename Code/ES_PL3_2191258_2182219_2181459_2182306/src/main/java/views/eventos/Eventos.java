package views.eventos;

import API.DatabaseConnector;
import API.EventosController;
import API.ViewBase;
import com.google.inject.Inject;
import model.Evento;
import model.SemDadosEventos;
import views.model.ModelCrud;
import views.model.ModelListRender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

public class Eventos implements ViewBase {
    private final DefaultListModel<ModelCrud<Evento>> eventosListModel = new DefaultListModel<>();
    private JPanel mainPanel;
    private JList<ModelCrud<Evento>> listEventos;
    private JButton buttonNovoEvento;
    private JButton btn_elimi;
    private JButton buttonPrograma;
    private JButton btn_importar;
    private JButton buttonVoltar;

    @Inject
    private EventosController eventosController;
    @Inject
    private DatabaseConnector databaseConnector;

    public Eventos() {
        setupButtons();
        setupList();
    }

    @Override
    public Container getViewContainer() {
        return mainPanel;
    }

    @Override
    public boolean prepareView() {
        eventosListModel.clear();
        Collection<Evento> eventos = databaseConnector.getEventos();
        if (eventos == null || eventos.isEmpty())
            eventosListModel.addElement(new ModelCrud<>(new SemDadosEventos()));
        else {
            for (Evento evento : eventos) {
                ModelCrud<Evento> listRow = new ModelCrud<>(evento, () -> eventosController.edit(evento), () -> eventosController.destroy(evento));
                eventosListModel.addElement(listRow);
            }
        }
        listEventos.clearSelection();

        return true;
    }

    @Override
    public void setupBackButton(Runnable buttonBackCallback) {
        buttonVoltar.addActionListener(e -> buttonBackCallback.run());
    }

    private void setupButtons() {
        buttonNovoEvento.addActionListener(e -> eventosController.create());
        buttonPrograma.addActionListener(e -> eventosController.mostrarPrograma());
    }

    private void setupList() {
        listEventos.setCellRenderer(new ModelListRender<>());
        listEventos.setModel(eventosListModel);
        listEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listEventos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = listEventos.locationToIndex(e.getPoint());
                ModelCrud<Evento> model = listEventos.getModel().getElementAt(index);
                if (model == null)
                    return;

                model.onModelPress(e.getX(), e.getY());
            }
        });
    }
}
