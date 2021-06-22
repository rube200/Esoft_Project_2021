package views.modalidades;

import API.CrudController;
import API.TipoDeContagem;
import model.Modalidade;

import javax.swing.*;

public class NovaModalidade extends JFrame {
    private static final String TITLE = "Adicionar Modalidade";

    private JPanel mainPanel;
    private JTextField inputNome;
    private JComboBox<TipoDeContagem> inputTipo;
    private JButton buttonGuardar;
    private JButton buttonCancelar;

    public NovaModalidade(CrudController<Modalidade> controller) {
        super(TITLE);

        setContentPane(mainPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setupButtons(controller);
        setupCombo();
    }

    private void setupButtons(CrudController<Modalidade> controller) {
        buttonCancelar.addActionListener(e -> dispose());
        buttonGuardar.addActionListener(e -> {
            String nome = inputNome.getText();
            if (nome.length() == 0) {
                controller.mostrarAviso("Introduza o nome da Modalidade!");
                return;
            }

            TipoDeContagem tipo = (TipoDeContagem) inputTipo.getSelectedItem();
            Modalidade modalidade = new Modalidade(nome, tipo);
            controller.store(modalidade);
            dispose();
        });
    }

    private void setupCombo() {
        for (TipoDeContagem tipo : TipoDeContagem.values()) {
            inputTipo.addItem(tipo);
        }
    }
}
