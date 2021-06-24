package controllers;

import API.CrudController;
import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import model.Evento;
import model.Modalidade;
import model.Prova;
import views.provas.NovaEditarProva;

import java.util.ArrayList;
import java.util.Collection;

public class ProvasController implements CrudController<Prova> {
    private final ViewBase provasView;
    @Inject
    private ViewController viewController;
    @Inject
    private DatabaseConnector databaseConnector;
    @Inject
    @Named("EventosController")
    private CrudController<Evento> eventosController;
    @Inject
    @Named("ModalidadesController")
    private CrudController<Modalidade> modalidadesController;

    @Inject
    public ProvasController(@Named("ProvasView") ViewBase provasView) {
        this.provasView = provasView;
        provasView.setupBackButton(this::onBack);
    }

    void onBack() {
        viewController.onBackRequested();
    }

    @Override
    public Prova create() {
        return createOrEdit(null);
    }

    @Override
    public void destroy(Prova prova) {
        try {
            if (!databaseConnector.delete(prova)) {
                return;
            }
            provasView.prepareView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void edit(Prova prova) {
        createOrEdit(prova);
    }

    private Prova createOrEdit(Prova prova) {
        Collection<Evento> eventos = databaseConnector.getEventoAtuaisOuFuturos();
        if (eventos == null || eventos.isEmpty()) {
            viewController.mostrarAviso("Não existem eventos guardados!");
            return null;
        }

        Collection<Modalidade> modalidades = databaseConnector.getModalidades();
        if (modalidades == null) {
            viewController.mostrarAviso("Falha ao ler modalidades!");
            return null;
        }
        if (modalidades.isEmpty()) {
            viewController.mostrarAviso("Não existem modalidades guardados!");
            return null;
        }

        NovaEditarProva dialog;
        if (prova != null)
            dialog = new NovaEditarProva(this, new ArrayList<>(eventos), modalidades, prova);
        else
            dialog = new NovaEditarProva(this, new ArrayList<>(eventos), modalidades);
        viewController.displayPopup(dialog);
        return dialog.getProva();
    }

    @Override
    public void index() {
        viewController.displayView(provasView);
    }

    @Override
    public void update(Prova prova) {
        try {
            if (!databaseConnector.update(prova)) {
                return;
            }
            provasView.prepareView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void store(Prova prova) {
        try {
            if (!databaseConnector.store(prova)) {
                return;
            }
            provasView.prepareView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void mostrarAviso(String mensagem) {
        viewController.mostrarAviso(mensagem);
    }

    public Evento novoEvento() {
        return eventosController.create();
    }

    public Modalidade novaModalidade() {
        return modalidadesController.create();
    }
}
