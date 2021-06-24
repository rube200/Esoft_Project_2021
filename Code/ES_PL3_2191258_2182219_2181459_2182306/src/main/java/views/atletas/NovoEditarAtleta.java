package views.atletas;

import API.CrudController;
import API.Sexo;
import model.Atleta;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NovoEditarAtleta extends JDialog {
    private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private JPanel mainPanel;
    private JTextField inputNome;
    private JTextField inputPais;
    private JComboBox<Sexo> inputSexo;
    private JFormattedTextField inputDataDeNascimento;
    private JTextField inputContacto;
    private JButton buttonGuardar;
    private JButton buttonCancelar;
    private int atletaId;
    private Atleta atleta;

    public NovoEditarAtleta(CrudController<Atleta> controller) {
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
        setupInputs();
    }

    public NovoEditarAtleta(CrudController<Atleta> controller, Atleta atleta) {
        this(controller);
        atletaId = atleta.getId();
        setupEditar(atleta);
    }

    private void setupButtons(CrudController<Atleta> controller) {
        buttonCancelar.addActionListener(e -> onCancel());
        buttonGuardar.addActionListener(e -> onGuardar(controller));
    }

    private void setupInputs() {
        for (Sexo sexo : Sexo.values()) {
            inputSexo.addItem(sexo);
        }

        DateFormatter formatter = new DateFormatter(DEFAULT_DATE_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 8);//Ano atual - 8 -> 8 anos de idade

        Date min = calendar.getTime();
        formatter.setMaximum(min);

        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 92);//Ano (atual - 8) - 92 -> 100 anos de idade, apesar de ninguem com 100 anos participar
        formatter.setMinimum(calendar.getTime());

        DefaultFormatterFactory factory = new DefaultFormatterFactory(formatter);
        inputDataDeNascimento.setFormatterFactory(factory);
        inputDataDeNascimento.setValue(min);
    }

    private void setupEditar(Atleta atleta) {
        inputNome.setText(atleta.getNome());
        inputPais.setText(atleta.getPais());
        inputSexo.setSelectedItem(atleta.getSexo());
        inputDataDeNascimento.setValue(atleta.getDataDeNascimento());
        inputContacto.setText(atleta.getContacto());
    }

    private void onCancel() {
        dispose();
    }

    private void onGuardar(CrudController<Atleta> controller) {
        String nome = inputNome.getText();
        if (nome.length() == 0) {
            controller.mostrarAviso("Introduza o nome do Atleta!");
            return;
        }

        if (nome.length() > 255) {
            controller.mostrarAviso("O nome do Atleta é muito longo!");
            return;
        }

        String pais = inputPais.getText();
        if (pais.length() == 0) {
            controller.mostrarAviso("Introduza o Pais do Atleta!");
            return;
        }

        if (pais.length() > 255) {
            controller.mostrarAviso("O nome do Pais é muito longo!");
            return;
        }

        Sexo sexo = (Sexo) inputSexo.getSelectedItem();
        Date dataDeNascimento = (Date) inputDataDeNascimento.getValue();
        if (dataDeNascimento == null) {
            controller.mostrarAviso("Data de nascimento inválida!");
            return;
        }

        String contacto = inputContacto.getText();
        if (contacto.length() == 0) {
            controller.mostrarAviso("Introduza o Contacto do Atleta!");
            return;
        }

        if (contacto.length() > 255) {
            controller.mostrarAviso("O contacto do Atleta é muito longo!");
            return;
        }

        atleta = new Atleta(nome, pais, sexo, dataDeNascimento, contacto);
        if (atletaId > 0) {
            atleta.setId(atletaId);
            controller.update(atleta);
        } else
            controller.store(atleta);

        dispose();
    }

    public Atleta getAtleta() {
        return atleta;
    }
}

