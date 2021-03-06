package controllers;

import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import model.Evento;
import views.eventos.NovoEditarEvento;

public class EventosController implements API.EventosController {
    private final ViewBase eventosView;
    private final ViewBase programaView;

    @Inject
    private ViewController viewController;
    @Inject
    private DatabaseConnector databaseConnector;

    @Inject
    public EventosController(@Named("EventosView") ViewBase eventosView, @Named("ProgramaView") ViewBase programaView) {
        this.eventosView = eventosView;
        this.programaView = programaView;

        eventosView.setupBackButton(this::onBack);
        programaView.setupBackButton(this::onBack);
    }

    private void onBack() {
        viewController.onBackRequested();
    }

    @Override
    public Evento create() {
        NovoEditarEvento dialog = new NovoEditarEvento(this);
        viewController.displayPopup(dialog);
        return dialog.getEvento();
    }

    @Override
    public void destroy(Evento evento) {
        try {
            if (!databaseConnector.delete(evento)) {
                return;
            }
            eventosView.prepareView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void edit(Evento evento) {
        viewController.displayPopup(new NovoEditarEvento(this, evento));
    }

    @Override
    public void index() {
        viewController.displayView(eventosView);
    }

    @Override
    public void update(Evento evento) {
        try {
            if (!databaseConnector.update(evento)) {
                return;
            }
            eventosView.prepareView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void store(Evento evento) {
        try {
            if (!databaseConnector.store(evento)) {
                return;
            }
            eventosView.prepareView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void mostrarAviso(String mensagem) {
        viewController.mostrarAviso(mensagem);
    }

    @Override
    public void mostrarPrograma() {
        viewController.displayView(programaView);
    }
}
