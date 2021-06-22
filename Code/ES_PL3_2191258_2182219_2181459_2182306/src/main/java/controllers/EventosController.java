package controllers;

import API.CrudController;
import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import model.Evento;
import views.eventos.NovoEvento;

//todo set list limite
//todo set enter and esc to guardar e cancelar
public class EventosController implements CrudController<Evento> {
    private final ViewBase eventosView;
    @Inject
    private ViewController viewController;
    @Inject
    public EventosController(@Named("EventosView") ViewBase eventosView) {
        this.eventosView = eventosView;
        eventosView.setupBackButton(this::onBack);
    }

    private void onBack() {
        viewController.onBackRequested();
    }

    @Override
    public void create() {
        viewController.displayPopup(new NovoEvento(this));
    }

    @Override
    public void index() {
        viewController.displayView(eventosView);
    }

    @Inject
    private DatabaseConnector databaseConnector;
    @Override
    public void store(Evento evento) {
        try {
            if (!databaseConnector.store(evento)) {
                //todo error show
                return;
            }
            eventosView.prepareView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
