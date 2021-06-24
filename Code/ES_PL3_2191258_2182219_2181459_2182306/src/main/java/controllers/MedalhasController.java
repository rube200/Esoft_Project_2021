package controllers;

import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class MedalhasController {
    private final ViewBase medalhasView;

    @Inject
    private ViewController viewController;

    @Inject
    public MedalhasController(@Named("MedalhasView") ViewBase medalhasView) {
        this.medalhasView = medalhasView;
    }

    public void mostrar() {
        viewController.displayView(medalhasView);
        medalhasView.setupBackButton(this::onBack);
    }

    private void onBack() {
        viewController.onBackRequested();
    }
}
