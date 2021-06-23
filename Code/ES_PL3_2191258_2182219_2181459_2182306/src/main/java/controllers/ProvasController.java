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

import java.util.Collection;

public class ProvasController implements CrudController<Prova> {
    private final ViewBase provasView;
    @Inject
    private ViewController viewController;

    @Inject
    public ProvasController(@Named("ProvasView") ViewBase provasView) {
        this.provasView = provasView;
        provasView.setupBackButton(this::onBack);
    }

    void onBack() {
        viewController.onBackRequested();
    }

    @Inject
    private DatabaseConnector databaseConnector;

    @Override
    public Prova create() {
        return createOrEdit(null);
    }

    @Override
    public void edit(Prova prova) {
        createOrEdit(prova);
    }
    private Prova createOrEdit(Prova prova) {
        Collection<Evento> eventos = databaseConnector.getEventoAtuaisOuFuturos();
        if (eventos == null) {
            viewController.mostrarAviso("Falha ao ler eventos!");
            return null;
        }
        if (eventos.isEmpty()) {
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
            dialog = new NovaEditarProva(this, eventos, modalidades, prova);
        else
            dialog = new NovaEditarProva(this, eventos, modalidades);
        viewController.displayPopup(dialog);
        return dialog.getProva();
    }

    @Override
    public void index() {
        viewController.displayView(provasView);
    }

    @Override
    public void store(Prova data) {
        try {
            if (!databaseConnector.store(data)) {
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

    @Inject
    @Named("EventosController")
    private CrudController<Evento> eventosController;
    public Evento novoEvento() {
        return eventosController.create();
    }

    @Inject
    @Named("ModalidadesController")
    private CrudController<Modalidade> modalidadesController;
    public Modalidade novaModalidade() {
        return modalidadesController.create();
    }
}
