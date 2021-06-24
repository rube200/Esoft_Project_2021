package views.eventos;

import API.CrudController;
import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import model.Evento;
import model.Prova;
import model.TodosDadosEventos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Calendar;
import java.util.Collection;

public class Programa implements ViewBase {
    private static final String[] COLUMN_NAMES = new String[]{
            "Prova",
            "Dia de Competição",
            "Dia e Hora"
    };
    private final DefaultTableModel tableProgramaModel = new DefaultTableModel();

    private JPanel mainPanel;
    private JTable tablePrograma;

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

    public Programa() {
        for (String column : COLUMN_NAMES) {
            tableProgramaModel.addColumn(column);
        }
        tablePrograma.setModel(tableProgramaModel);
        inputEvento.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED)
                return;

            Evento evento = (Evento) e.getItem();
            if (evento == null || evento.getId() < 1)
                return;

            while (tableProgramaModel.getRowCount() > 0) {
                tableProgramaModel.removeRow(0);
            }

            Collection<Prova> provas = databaseConnector.getProvas(evento);
            if (provas == null || provas.isEmpty())
                return;

            for (Prova prova : provas) {
                tableProgramaModel.addRow(new Object[]{prova.getNome(), prova.getDiaDeCompeticao(), prova.getDataDaProva()});
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

        while (tableProgramaModel.getRowCount() > 0) {
            tableProgramaModel.removeRow(0);
        }

        Collection<Prova> provas = databaseConnector.getProvas();
        if (provas == null || provas.isEmpty())
            return true;
        for (Prova prova : provas) {
            tableProgramaModel.addRow(new Object[]{prova.getNome(), prova.getDiaDeCompeticao(), prova.getDataDaProva()});
        }

        return true;
    }

    @Override
    public void setupBackButton(Runnable buttonBackCallback) {
        buttonVoltar.addActionListener(e -> buttonBackCallback.run());
    }
}
