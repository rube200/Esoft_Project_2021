package views.provas;

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
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

public class NovaProva extends JFrame {
    private static final String TITLE = "Adicionar Prova";
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

    public NovaProva(ProvasController controller, Collection<Evento> eventos, Collection<Modalidade> modalidades) {
        super(TITLE);

        setContentPane(mainPanel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setupButtons(controller);
        setupInputs(eventos, modalidades);
    }

    private void setupButtons(ProvasController controller) {
        buttonCancelar.addActionListener(e -> dispose());
        buttonGuardar.addActionListener(e -> {
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
            Prova prova = new Prova(evento.getId(), modalidade.getId(), sexo, minimos, atletasPorRonda);
            controller.store(prova);
            dispose();
        });
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

    private void onComboChange(ItemEvent item) {
        if (item.getStateChange() != ItemEvent.SELECTED)
            return;

        Object selectedItem = item.getItem();
        if (!(selectedItem instanceof UniqueId uniqueIdObj))
            return;

        switch (uniqueIdObj.getId())
        {
            case NOVO_EVENTO_ID:

                break;

            case NOVA_MODALIDADE_ID:

                break;
        }
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
