package views.provas;

import API.CrudController;
import API.Sexo;
import controllers.ProvasController;
import model.Evento;
import model.Modalidade;
import model.Prova;
import model.UniqueId;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

public class NovaEditarProva extends JDialog {
    private static final int NOVO_EVENTO_ID = -1;
    private static final int NOVA_MODALIDADE_ID = -2;
    private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private JPanel mainPanel;

    //todo verify date and hora
    private JComboBox<Evento> inputEvento;
    private JComboBox<Modalidade> inputModalidade;
    private JTextField inputDiaDeCompeticao;
    private JComboBox<Sexo> inputSexo;
    private JFormattedTextField inputAtletasPorRonda;
    private JFormattedTextField inputData;
    private JTextField inputHora;
    private JFormattedTextField inputMinimos;
    private JButton buttonGuardar;
    private JButton buttonCancelar;

    private final ProvasController controller;
    public NovaEditarProva(ProvasController controller, Collection<Evento> eventos, Collection<Modalidade> modalidades) {
        this.controller = controller;

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
        setupInputs(eventos, modalidades);
    }

    private int provaId;
    public NovaEditarProva(ProvasController controller, Collection<Evento> eventos, Collection<Modalidade> modalidades, Prova prova) {
        this(controller, eventos, modalidades);
        this.provaId = prova.getId();
        setupEditar(prova);
    }

    private void setupButtons(CrudController<Prova> controller) {
        buttonCancelar.addActionListener(e -> onCancel());
        buttonGuardar.addActionListener(e -> onGuardar(controller));
    }

    private void setupInputs(Collection<Evento> eventos, Collection<Modalidade> modalidades) {
        for (Evento evento : eventos) {
            inputEvento.addItem(evento);
        }
        inputEvento.addItem(new NovoEventoModel());
        inputEvento.addItemListener(this::onComboChange);

        for (Modalidade modalidade : modalidades) {
            inputModalidade.addItem(modalidade);
        }
        inputModalidade.addItem(new NovaModalidadeModel());
        inputModalidade.addItemListener(this::onComboChange);

        for (Sexo sexo : Sexo.values()) {
            inputSexo.addItem(sexo);
        }

        NumberFormat integerFormat = NumberFormat.getNumberInstance();
        integerFormat.setMinimumIntegerDigits(1);
        integerFormat.setMaximumIntegerDigits(1);

        NumberFormatter numberFormatter = new NumberFormatter(integerFormat);
        numberFormatter.setMinimum(0);

        DefaultFormatterFactory factory = new DefaultFormatterFactory(numberFormatter);
        inputMinimos.setFormatterFactory(factory);
        inputMinimos.setValue(0);

        numberFormatter = new NumberFormatter(integerFormat);
        numberFormatter.setMinimum(2);
        numberFormatter.setMaximum(8);

        factory = new DefaultFormatterFactory(numberFormatter);
        inputAtletasPorRonda.setFormatterFactory(factory);
        inputAtletasPorRonda.setValue(8);

        /*DefaultFormatter dateFormatter = new DateFormatter(DEFAULT_DATE_FORMAT);
        factory = new DefaultFormatterFactory(dateFormatter);

        inputData.setFormatterFactory(factory);
        inputData.setValue(new Date());

        inputHora.setFormatterFactory(factory);
        inputHora.setValue(new Date());*/
    }

    private Object lastComboItem;
    private void onComboChange(ItemEvent item) {
        int state = item.getStateChange();
        if (state == ItemEvent.DESELECTED) {
            lastComboItem = item.getItem();
            return;
        }

        if (item.getStateChange() != ItemEvent.SELECTED)
            return;

        Object selectedItem = item.getItem();
        if (!(selectedItem instanceof UniqueId uniqueIdObj))
            return;

        int index;
        switch (uniqueIdObj.getId()) {
            case NOVO_EVENTO_ID -> {
                inputEvento.setSelectedItem(lastComboItem);
                Evento evento = controller.novoEvento();
                if (evento == null) {
                    inputEvento.setSelectedIndex(0);
                    break;
                }
                index = inputEvento.getModel().getSize() - 1;
                inputEvento.insertItemAt(evento, index);
                inputEvento.setSelectedIndex(index);
            }
            case NOVA_MODALIDADE_ID -> {
                inputModalidade.setSelectedItem(lastComboItem);
                Modalidade modalidade = controller.novaModalidade();
                if (modalidade == null) {
                    inputModalidade.setSelectedIndex(0);
                    break;
                }
                index = inputModalidade.getModel().getSize() - 1;
                inputModalidade.insertItemAt(modalidade, index);
                inputModalidade.setSelectedIndex(index);
            }
        }
    }

    private void setupEditar(Prova prova) {
        int size = inputEvento.getModel().getSize();
        for (int i = 0; i < size; i++) {
            Evento evento = inputEvento.getItemAt(i);
            if (evento.getId() != prova.getEventoId())
                continue;

            inputEvento.setSelectedIndex(i);
            break;
        }

        size = inputModalidade.getModel().getSize();
        for (int i = 0; i < size; i++) {
            Modalidade modalidade = inputModalidade.getItemAt(i);
            if (modalidade.getId() != prova.getModalidadeId())
                continue;

            inputModalidade.setSelectedIndex(i);
            break;
        }

        inputSexo.setSelectedItem(prova.getSexo());
        inputMinimos.setValue(prova.getMinimos());
        inputAtletasPorRonda.setValue(prova.getAtletasPorProva());
    }

    private void onCancel() {
        dispose();
    }

    private Prova prova;
    private void onGuardar(CrudController<Prova> controller) {
        Evento evento = (Evento) inputEvento.getSelectedItem();
        if (evento == null || evento.getId() < 0) {
            controller.mostrarAviso("Evento não encontrado.");
            return;
        }

        Modalidade modalidade = (Modalidade) inputModalidade.getSelectedItem();
        if (modalidade == null || modalidade.getId() < 0) {
            controller.mostrarAviso("Modalidade não encontrada.");
            return;
        }

        String diaDeCompeticao = inputDiaDeCompeticao.getText();
        if (diaDeCompeticao.length() == 0) {
            controller.mostrarAviso("Introduza o dia de competição");
            return;
        }

        Sexo sexo = (Sexo) inputSexo.getSelectedItem();
        int minimos = (int)inputMinimos.getValue();
        if (minimos <= 0) {
            controller.mostrarAviso("Introduza um mínimo de acesso válido: " + minimos);
            return;
        }

        byte atletasPorRonda = (byte)(int)inputAtletasPorRonda.getValue();
        prova = new Prova(evento.getId(), modalidade.getId(), sexo, minimos, atletasPorRonda);

        if (provaId > 0)
            prova.setId(provaId);

        controller.store(prova);
        dispose();
    }

    public Prova getProva() {
        return prova;
    }

    private static class NovoEventoModel extends Evento {
        private NovoEventoModel() {
            super(NOVO_EVENTO_ID, "Adicionar novo Evento");
        }

        @Override
        public String toString() {
            return getNome();
        }
    }
    private static class NovaModalidadeModel extends Modalidade {
        private NovaModalidadeModel() {
            super(NOVA_MODALIDADE_ID, "Adicionar nova Modalidade");
        }

        @Override
        public String toString() {
            return getNome();
        }
    }
}
