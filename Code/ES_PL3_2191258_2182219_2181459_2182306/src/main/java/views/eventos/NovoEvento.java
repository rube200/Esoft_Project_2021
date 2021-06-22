package views.eventos;

import API.CrudController;
import model.Evento;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NovoEvento extends JFrame {
    private static final String TITLE = "Adicionar Evento";
    private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private JPanel mainPanel;
    private JTextField inputNome;
    private JFormattedTextField inputInicio;
    private JFormattedTextField inputFim;
    private JTextField inputPais;
    private JTextField inputLocal;
    private JButton buttonGuardar;
    private JButton buttonCancelar;
    private JTextField a;

    public NovoEvento(CrudController<Evento> controller) {
        super(TITLE);

        setContentPane(mainPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setupButtons(controller);
        setupDateInputs();
    }

    private void setupButtons(CrudController<Evento> controller) {
        buttonCancelar.addActionListener(e -> dispose());
        buttonGuardar.addActionListener(e -> {
            String nome = inputNome.getText();
            if (nome.length() == 0) {
                //todo invalid name
                return;
            }

            Date inicio = (Date) inputInicio.getValue();
            if (inicio == null) {
                //todo invalid inicio
                return;
            }

            Date fim = (Date) inputFim.getValue();
            if (fim == null) {
                //todo invalid inicio
                return;
            }

            String pais = inputPais.getText();
            if (pais.length() == 0) {
                //todo invalid pais
                return;
            }

            String local = inputLocal.getText();
            if (local.length() == 0) {
                //todo invalid local
                return;
            }

            Evento evento = new Evento(nome, inicio, fim, pais, local);
            controller.store(evento);
            dispose();
        });
    }

    private void setupDateInputs() {
        DefaultFormatter formatter = new DateFormatter(DEFAULT_DATE_FORMAT);
        DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);

        inputInicio.setFormatterFactory(factory);
        inputInicio.setValue(new Date());
        inputFim.setFormatterFactory(factory);
        inputFim.setValue(new Date());
    }
}
