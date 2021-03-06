package views.provas;

import API.CrudController;
import API.Sexo;
import controllers.ProvasController;
import model.Evento;
import model.Modalidade;
import model.Prova;
import model.UniqueId;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class NovaEditarProva extends JDialog {
    private static final int NOVO_EVENTO_ID = -1;
    private static final int NOVA_MODALIDADE_ID = -2;
    private static final DateFormatter DEFAULT_DATE_FORMATTER = new DateFormatter(new SimpleDateFormat("dd/MM/yyyy"));
    private static final DateFormat DEFAULT_TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final DateFormatter DEFAULT_TIME_FORMATTER = new DateFormatter(DEFAULT_TIME_FORMAT);
    private final ProvasController controller;
    private JPanel mainPanel;
    private JComboBox<Evento> inputEvento;
    private JComboBox<Modalidade> inputModalidade;
    private JTextField inputDiaDeCompeticao;
    private JComboBox<Sexo> inputSexo;
    private JFormattedTextField inputAtletasPorRonda;
    private JFormattedTextField inputData;
    private JFormattedTextField inputHora;
    private JFormattedTextField inputMinimos;
    private JButton buttonGuardar;
    private JButton buttonCancelar;
    private int provaId;
    private Object lastComboItem;
    private Prova prova;

    public NovaEditarProva(ProvasController controller, List<Evento> eventos, Collection<Modalidade> modalidades) {
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

    public NovaEditarProva(ProvasController controller, List<Evento> eventos, Collection<Modalidade> modalidades, Prova prova) {
        this(controller, eventos, modalidades);
        this.provaId = prova.getId();
        setupEditar(prova);
    }

    private void setupButtons(CrudController<Prova> controller) {
        buttonCancelar.addActionListener(e -> onCancel());
        buttonGuardar.addActionListener(e -> onGuardar(controller));
    }

    private void setupInputs(List<Evento> eventos, Collection<Modalidade> modalidades) {
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

        inputDiaDeCompeticao.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String diaDaComp = inputDiaDeCompeticao.getText();
                if (diaDaComp.isBlank())
                    return;

                Evento evento = (Evento) inputEvento.getSelectedItem();
                if (evento == null || evento.getId() < 1)
                    return;

                try {
                    int dia = Integer.parseInt(diaDaComp);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(evento.getInicio());
                    cal.add(Calendar.DATE, dia);
                    Date newDate = cal.getTime();
                    if (newDate.getTime() > evento.getFimTime())
                        return;

                    inputData.setValue(newDate);
                } catch (Exception ignored) {
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

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

        Evento evento = eventos.get(0);
        DEFAULT_DATE_FORMATTER.setMinimum(evento.getInicio());
        DEFAULT_DATE_FORMATTER.setMaximum(evento.getFim());
        factory = new DefaultFormatterFactory(DEFAULT_DATE_FORMATTER);

        inputData.setFormatterFactory(factory);
        if (evento.getInicioTime() < new Date().getTime())
            inputData.setValue(evento.getFim());

        try {
            Date min = DEFAULT_TIME_FORMAT.parse("00:00");
            DEFAULT_TIME_FORMATTER.setMinimum(min);
            DEFAULT_TIME_FORMATTER.setMaximum(DEFAULT_TIME_FORMAT.parse("23:59"));
            factory = new DefaultFormatterFactory(DEFAULT_TIME_FORMATTER);

            inputHora.setFormatterFactory(factory);
            inputHora.setValue(min);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

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

        if (selectedItem instanceof Evento evento) {
            DEFAULT_DATE_FORMATTER.setMinimum(evento.getInicio());
            DEFAULT_DATE_FORMATTER.setMaximum(evento.getFim());
            DefaultFormatterFactory factory = new DefaultFormatterFactory(DEFAULT_DATE_FORMATTER);

            inputData.setFormatterFactory(factory);
            if (evento.getInicioTime() < new Date().getTime())
                inputData.setValue(evento.getFim());
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

        inputDiaDeCompeticao.setText(prova.getDiaDeCompeticao());
        inputSexo.setSelectedItem(prova.getSexo());
        inputMinimos.setValue(prova.getMinimos());
        inputAtletasPorRonda.setValue((int) prova.getAtletasPorProva());
        inputData.setValue(prova.getDataDaProva());
        inputHora.setValue(prova.getDataDaProva());
    }

    private void onCancel() {
        dispose();
    }

    private void onGuardar(CrudController<Prova> controller) {
        Evento evento = (Evento) inputEvento.getSelectedItem();
        if (evento == null || evento.getId() < 1) {
            controller.mostrarAviso("Evento n??o encontrado.");
            return;
        }

        Modalidade modalidade = (Modalidade) inputModalidade.getSelectedItem();
        if (modalidade == null || modalidade.getId() < 0) {
            controller.mostrarAviso("Modalidade n??o encontrada.");
            return;
        }

        String diaDeCompeticao = inputDiaDeCompeticao.getText();
        if (diaDeCompeticao.isBlank()) {
            controller.mostrarAviso("Introduza o dia de competi????o");
            return;
        }

        if (diaDeCompeticao.length() > 25) {
            controller.mostrarAviso("O dia de competi????o ?? muito longo!");
            return;
        }

        Sexo sexo = (Sexo) inputSexo.getSelectedItem();
        int minimos = (int) inputMinimos.getValue();
        if (minimos <= 0) {
            controller.mostrarAviso("Introduza um m??nimo de acesso v??lido!");
            return;
        }

        Date dataDeProva = (Date) inputData.getValue();
        if (dataDeProva == null) {
            controller.mostrarAviso("A data da prova ?? inv??lida!");
            return;
        }

        Date horaDeProva = (Date) inputHora.getValue();
        if (horaDeProva == null) {
            controller.mostrarAviso("A hora da prova ?? inv??lida!");
            return;
        }

        Calendar cal = Calendar.getInstance();
        //noinspection deprecation
        cal.set(dataDeProva.getYear() + 1900, dataDeProva.getMonth(), dataDeProva.getDate(), horaDeProva.getHours(), horaDeProva.getMinutes(), 0);

        byte atletasPorRonda = (byte) (int) inputAtletasPorRonda.getValue();
        prova = new Prova(evento.getId(), modalidade.getId(), diaDeCompeticao, sexo, minimos, atletasPorRonda, cal.getTime());
        if (provaId > 0) {
            prova.setId(provaId);
            controller.update(prova);
        } else
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
