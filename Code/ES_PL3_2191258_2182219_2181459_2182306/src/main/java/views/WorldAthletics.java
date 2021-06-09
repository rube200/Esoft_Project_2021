package views;

import com.google.inject.Inject;
import model.Evento;
import model.Prova;

import javax.persistence.EntityManager;
import javax.swing.*;
import java.util.List;

public class WorldAthletics extends JFrame {
    private JPanel mainPanel;
    private JList<Evento> listEventos;//setLayoutOrientation(JList.HORIZONTAL_WRAP);
    private final DefaultListModel<Evento> eventosListModel = new DefaultListModel<>();
    private JList<Prova> listProvas;
    private final DefaultListModel<Prova> provasListModel = new DefaultListModel<>();

    private JButton button1;
    private JButton buttonAtletas;
    private JButton buttonModalidades;
    private JButton buttonInscreverAtleta;
    private JButton buttonMedalhas;
    private JButton buttonTendencias;
    private JButton buttonGerirEventos;
    private JButton buttonGerirProvas;
    private JButton buttonSair;

    @Inject
    private EntityManager entityManager;

    public WorldAthletics() {
        super("World Athletics");
        setContentPane(mainPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        listEventos.setModel(eventosListModel);
        listEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listProvas.setModel(provasListModel);
        listProvas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void prepareData() {
        eventosListModel.addAll(queryAll(Evento.class));
        provasListModel.addAll(queryAll(Prova.class));
    }

    public void openView() {
        pack();
        this.setVisible(true);
    }

    private <X> List<X> queryAll(Class<X> entityClass) {

        var builder = entityManager.getCriteriaBuilder();
        var query = builder.createQuery(entityClass);
        var allQUERY = query.select(query.from(entityClass));
        return entityManager.createQuery(allQUERY).getResultList();
    }
}

