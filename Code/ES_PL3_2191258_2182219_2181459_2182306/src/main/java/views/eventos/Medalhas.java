package views.eventos;

import API.CrudController;
import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import model.Evento;
import model.TodosDadosEventos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Calendar;
import java.util.Collection;

public class Medalhas implements ViewBase {
    private static final String[] COLUMN_NAMES = new String[]{
            "Pais",
            "Ouro",
            "Prata",
            "Bronze"
    };
    private final DefaultTableModel tableMedalhasModel = new DefaultTableModel();

    private JPanel mainPanel;
    private JTable tableMedalhas;

    private JComboBox<Evento> inputEvento;
    private JCheckBox eventoAtual;

    private JButton buttonVoltar;

    @Inject
    @Named("EventosController")
    private CrudController<Evento> eventosController;
    @Inject
    private DatabaseConnector databaseConnector;
    private long today;
    private Evento firstEventoAtual;

    public Medalhas() {
        for (String column : COLUMN_NAMES) {
            tableMedalhasModel.addColumn(column);
        }
        tableMedalhas.setModel(tableMedalhasModel);
        inputEvento.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED)
                return;

            Evento evento = (Evento) e.getItem();
            if (evento == null || evento.getId() < 1)
                return;

            while (tableMedalhasModel.getRowCount() > 0) {
                tableMedalhasModel.removeRow(0);
            }

            Collection<model.Medalhas> medalhas = databaseConnector.getMedalhados(evento);
            if (medalhas == null || medalhas.isEmpty())
                return;

            for (model.Medalhas medalha : medalhas) {
                tableMedalhasModel.addRow(new Object[]{medalha.getNome(), medalha.getOuro(), medalha.getPrata(), medalha.getBronze()});
            }
        });
        eventoAtual.addActionListener(e -> onChecked());
    }

    @SuppressWarnings("DuplicatedCode")
    private void onChecked() {
        if (!eventoAtual.isSelected() || firstEventoAtual == null)
            return;

        Evento evento = (Evento) inputEvento.getSelectedItem();
        if (evento != null && evento.getId() >= 1) {
            if (evento.getInicioTime() <= today && today <= evento.getFimTime())
                return;
        }

        inputEvento.setSelectedItem(firstEventoAtual);
    }

    @Override
    public Container getViewContainer() {
        return mainPanel;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public boolean prepareView() {
        eventoAtual.setSelected(false);
        firstEventoAtual = null;

        Collection<Evento> eventos = databaseConnector.getEventos();
        if (eventos == null || eventos.isEmpty()) {
            eventosController.mostrarAviso("Não existem eventos guardados!");
            return false;
        }

        inputEvento.removeAllItems();
        inputEvento.addItem(new TodosDadosEventos());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        today = calendar.getTime().getTime();
        for (Evento evento : eventos) {
            inputEvento.addItem(evento);
            if (firstEventoAtual == null && evento.getInicioTime() <= today && today <= evento.getFimTime())
                firstEventoAtual = evento;
        }

        if (firstEventoAtual == null) {
            if (eventoAtual.isEnabled())
                eventoAtual.setEnabled(false);
        } else {
            if (!eventoAtual.isEnabled())
                eventoAtual.setEnabled(true);
        }

        while (tableMedalhasModel.getRowCount() > 0) {
            tableMedalhasModel.removeRow(0);
        }

        Collection<model.Medalhas> medalhas = databaseConnector.getMedalhados();
        if (medalhas == null || medalhas.isEmpty())
            return true;

        for (model.Medalhas medalha : medalhas) {
            tableMedalhasModel.addRow(new Object[]{medalha.getNome(), medalha.getOuro(), medalha.getPrata(), medalha.getBronze()});
        }

        return true;
    }

    @Override
    public void setupBackButton(Runnable buttonBackCallback) {
        buttonVoltar.addActionListener(e -> buttonBackCallback.run());
    }
}
