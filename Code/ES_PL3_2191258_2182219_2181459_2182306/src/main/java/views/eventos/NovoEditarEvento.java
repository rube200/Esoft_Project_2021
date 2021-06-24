package views.eventos;

import API.CrudController;
import model.Evento;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NovoEditarEvento extends JDialog {
    private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private JPanel mainPanel;
    private JTextField inputNome;
    private JFormattedTextField inputInicio;
    private JFormattedTextField inputFim;
    private JTextField inputPais;
    private JTextField inputLocal;
    private JButton buttonGuardar;
    private JButton buttonCancelar;
    private int eventoId;
    private Evento evento;

    public NovoEditarEvento(CrudController<Evento> controller) {
        setContentPane(mainPanel);
        setModal(true);
        getRootPane().setDefaultButton(buttonGuardar);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        mainPanel.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setupButtons(controller);
        setupDateInputs();
    }

    public NovoEditarEvento(CrudController<Evento> controller, Evento evento) {
        this(controller);
        eventoId = evento.getId();
        setupEditar(evento);
    }

    private void setupButtons(CrudController<Evento> controller) {
        buttonCancelar.addActionListener(e -> onCancel());
        buttonGuardar.addActionListener(e -> onGuardar(controller));
    }

    private void setupDateInputs() {
        DateFormatter formatter = new DateFormatter(DEFAULT_DATE_FORMAT);
        formatter.setMinimum(new Date());
        DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);

        inputInicio.setFormatterFactory(factory);
        inputInicio.setValue(new Date());
        inputFim.setFormatterFactory(factory);
        inputFim.setValue(new Date());
    }

    private void setupEditar(Evento evento) {
        inputNome.setText(evento.getNome());
        inputInicio.setValue(evento.getInicio());
        inputFim.setValue(evento.getFim());
        inputPais.setText(evento.getPais());
        inputLocal.setText(evento.getLocal());
    }

    private void onCancel() {
        dispose();
    }

    private void onGuardar(CrudController<Evento> controller) {
        String nome = inputNome.getText();
        if (nome.isBlank()) {
            controller.mostrarAviso("Introduza o nome do Evento!");
            return;
        }

        if (nome.length() > 100) {
            controller.mostrarAviso("O nome do Evento é muito longo!");
            return;
        }

        try {
            Integer.parseInt(nome);
            controller.mostrarAviso("O nome do Evento é inválido!");
            return;
        } catch (Exception ignored) {
        }

        Date inicio = (Date) inputInicio.getValue();
        if (inicio == null) {
            controller.mostrarAviso("Data de inicio inválida!");
            return;
        }

        Date fim = (Date) inputFim.getValue();
        if (fim == null) {
            controller.mostrarAviso("Data de fim inválida!");
            return;
        }

        if (inicio.getTime() > fim.getTime()) {
            controller.mostrarAviso("A data de inicio não pode ser superior a data de fim do Evento!");
            return;
        }

        String pais = inputPais.getText();
        if (pais.isBlank()) {
            controller.mostrarAviso("Introduza o Pais do Evento!");
            return;
        }

        if (pais.length() > 75) {
            controller.mostrarAviso("O nome do Pais é muito longo!");
            return;
        }

        try {
            Integer.parseInt(pais);
            controller.mostrarAviso("O nome do Pais é inválido!");
            return;
        } catch (Exception ignored) {
        }

        String local = inputLocal.getText();
        if (local.isBlank()) {
            controller.mostrarAviso("Introduza o Local d  Evento!");
            return;
        }

        if (local.length() > 75) {
            controller.mostrarAviso("O local do Evento é muito longo!");
            return;
        }

        try {
            Integer.parseInt(local);
            controller.mostrarAviso("O local do Evento é inválido!");
            return;
        } catch (Exception ignored) {
        }

        evento = new Evento(nome, inicio, fim, pais, local);
        if (eventoId > 0) {
            evento.setId(eventoId);
            controller.update(evento);
        } else
            controller.store(evento);

        dispose();
    }

    public Evento getEvento() {
        return evento;
    }
}
