package views;

import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import model.Evento;
import model.UniqueId;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Collection;

public class Medalhas implements ViewBase {
    private static final int TODOS_EVENTOS_ID = -3;

    private JPanel mainPanel;
    private JTable table1;
    private JComboBox<Evento> inputEvento;
    private JCheckBox eventoAtual;
    private JButton buttonVoltar;

    public Medalhas() {
        inputEvento.addItemListener(this::onComboChange);
    }

    @Inject
    private DatabaseConnector databaseConnector;

    @Override
    public Container getViewContainer() {
        return mainPanel;
    }

    @Override
    public boolean prepareView() {
        Collection<Evento> eventos = databaseConnector.getEventoAtuaisOuAnteriores();
        if (eventos == null)
            return false;

        inputEvento.removeAllItems();
        inputEvento.addItem(new TodosEventosModel());
        for (Evento evento : eventos) {
            inputEvento.addItem(evento);
        }

        return true;
    }

    @Override
    public void setupBackButton(Runnable buttonBackCallback) {
        buttonVoltar.addActionListener(e -> buttonBackCallback.run());
    }

    private void onComboChange(ItemEvent item) {
        if (item.getStateChange() != ItemEvent.SELECTED)
            return;

        Object selectedItem = item.getItem();
        if (!(selectedItem instanceof UniqueId uniqueIdObj) || uniqueIdObj.getId() != TODOS_EVENTOS_ID)
            return;

        //todo todos
    }

    private static class TodosEventosModel extends Evento {
        private TodosEventosModel() {
            super(TODOS_EVENTOS_ID, "Todos os eventos");
        }

        @Override
        public String toString() {
            return getNome();
        }
    }
}
