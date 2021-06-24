package views.modalidades;

import API.CrudController;
import API.TipoDeContagem;
import model.Modalidade;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class NovaEditarModalidade extends JDialog {
    private JPanel mainPanel;
    private JTextField inputNome;
    private JComboBox<TipoDeContagem> inputTipo;
    private JButton buttonGuardar;
    private JButton buttonCancelar;
    private int modalidadeId;
    private Modalidade modalidade;

    public NovaEditarModalidade(CrudController<Modalidade> controller) {
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
        setupCombo();
    }

    public NovaEditarModalidade(CrudController<Modalidade> controller, Modalidade modalidade) {
        this(controller);
        modalidadeId = modalidade.getId();
        setupEditar(modalidade);
    }

    private void setupButtons(CrudController<Modalidade> controller) {
        buttonCancelar.addActionListener(e -> onCancel());
        buttonGuardar.addActionListener(e -> onGuardar(controller));
    }

    private void setupCombo() {
        for (TipoDeContagem tipo : TipoDeContagem.values()) {
            inputTipo.addItem(tipo);
        }
    }

    private void setupEditar(Modalidade modalidade) {
        inputNome.setText(modalidade.getNome());
        inputTipo.setSelectedItem(modalidade.getTipoDeContagem());
    }

    private void onCancel() {
        dispose();
    }

    private void onGuardar(CrudController<Modalidade> controller) {
        String nome = inputNome.getText();
        if (nome.isBlank()) {
            controller.mostrarAviso("Introduza o nome da Modalidade!");
            return;
        }

        if (nome.length() > 50) {
            controller.mostrarAviso("O nome da Modalidade é muito longo!");
            return;
        }

        try {
            Integer.parseInt(nome);
            controller.mostrarAviso("O nome da Modalidade é inválido!");
            return;
        } catch (Exception ignored) {
        }

        TipoDeContagem tipo = (TipoDeContagem) inputTipo.getSelectedItem();
        modalidade = new Modalidade(nome, tipo);

        if (modalidadeId > 0) {
            modalidade.setId(modalidadeId);
            controller.update(modalidade);
        } else
            controller.store(modalidade);

        dispose();
    }

    public Modalidade getModalidade() {
        return modalidade;
    }
}
