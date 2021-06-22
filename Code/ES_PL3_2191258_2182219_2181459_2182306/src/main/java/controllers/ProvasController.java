package controllers;

import API.CrudController;
import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import model.Evento;
import model.Modalidade;
import model.Prova;
import views.provas.NovaProva;

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
    public void create() {
        Collection<Evento> eventos = databaseConnector.getEventoAtuaisOuFuturos();
        if (eventos == null) {
            viewController.mostrarAviso("Falha ao ler eventos!");
            return;
        }
        if (eventos.isEmpty()) {
            viewController.mostrarAviso("Não existem eventos guardados!");
            return;
        }

        Collection<Modalidade> modalidades = databaseConnector.getModalidades();
        if (modalidades == null) {
            viewController.mostrarAviso("Falha ao ler modalidades!");
            return;
        }
        if (modalidades.isEmpty()) {
            viewController.mostrarAviso("Não existem modalidades guardados!");
            return;
        }

        viewController.displayPopup(new NovaProva(this, eventos, modalidades));
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
}
