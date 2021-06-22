package controllers;

import API.CrudController;
import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import model.Prova;

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

    @Override
    public void create() {

    }

    @Override
    public void index() {
        viewController.displayView(provasView);
    }

    @Inject
    private DatabaseConnector databaseConnector;
    @Override
    public void store(Prova data) {
        try {
            if (!databaseConnector.store(data)) {
                //todo error show
                return;
            }
            provasView.prepareView();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
