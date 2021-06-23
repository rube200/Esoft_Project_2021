package controllers;

import API.CrudController;
import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import model.Modalidade;
import views.modalidades.NovaEditarModalidade;

public class ModalidadesController implements CrudController<Modalidade> {
    private final ViewBase modalidadesView;

    @Inject
    private ViewController viewController;

    @Inject
    public ModalidadesController(@Named("ModalidadesView") ViewBase modalidadesView) {
        this.modalidadesView = modalidadesView;
        modalidadesView.setupBackButton(this::onBack);
    }

    void onBack() {
        viewController.onBackRequested();
    }

    @Override
    public Modalidade create() {
        NovaEditarModalidade dialog = new NovaEditarModalidade(this);
        viewController.displayPopup(dialog);
        return dialog.getModalidade();
    }

    @Override
    public void edit(Modalidade modalidade) {
        viewController.displayPopup(new NovaEditarModalidade(this, modalidade));
    }

    @Override
    public void index() {
        viewController.displayView(modalidadesView);
    }

    @Inject
    private DatabaseConnector databaseConnector;
    @Override
    public void store(Modalidade data) {
        try {
            if (!databaseConnector.store(data)) {
                return;
            }
            modalidadesView.prepareView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void mostrarAviso(String mensagem) {
        viewController.mostrarAviso(mensagem);
    }
}
