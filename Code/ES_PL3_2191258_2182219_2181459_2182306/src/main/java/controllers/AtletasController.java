package controllers;

import API.CrudController;
import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import model.Atleta;
import views.atletas.NovoEditarAtleta;

public class AtletasController implements CrudController<Atleta> {
    private final ViewBase atletasView;

    @Inject
    private ViewController viewController;
    @Inject
    private DatabaseConnector databaseConnector;

    @Inject
    public AtletasController(@Named("AtletasView") ViewBase atletasView) {
        this.atletasView = atletasView;
        atletasView.setupBackButton(this::onBack);
    }

    private void onBack() {
        viewController.onBackRequested();
    }

    @Override
    public Atleta create() {
        NovoEditarAtleta dialog = new NovoEditarAtleta(this);
        viewController.displayPopup(dialog);
        return dialog.getAtleta();
    }

    @Override
    public void destroy(Atleta atleta) {
        try {
            if (!databaseConnector.delete(atleta)) {
                return;
            }
            atletasView.prepareView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void edit(Atleta atleta) {
        viewController.displayPopup(new NovoEditarAtleta(this, atleta));
    }

    @Override
    public void index() {
        viewController.displayView(atletasView);
    }

    @Override
    public void update(Atleta atleta) {
        try {
            if (!databaseConnector.update(atleta)) {
                return;
            }
            atletasView.prepareView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void store(Atleta atleta) {
        try {
            if (!databaseConnector.store(atleta)) {
                return;
            }
            atletasView.prepareView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void mostrarAviso(String mensagem) {
        viewController.mostrarAviso(mensagem);
    }
}
