package controllers;

import API.DatabaseConnector;
import API.ViewBase;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import model.Atleta;
import model.Prova;
import views.inscricoes.InscreverAtleta;
import views.inscricoes.InscreverEmProva;

import java.util.List;
import java.util.Map;

public class InscricoesController implements API.InscricoesController {
    private final InscreverAtleta inscreverAtleta;
    private final InscreverEmProva inscreverEmProva;
    private final ViewBase inscricoesView;

    @Inject
    private ViewController viewController;
    @Inject
    private DatabaseConnector databaseConnector;

    @Inject
    public InscricoesController(InscreverAtleta inscreverAtleta, InscreverEmProva inscreverEmProva, @Named("InscricoesView") ViewBase inscricoesView) {
        this.inscreverAtleta = inscreverAtleta;
        this.inscreverEmProva = inscreverEmProva;
        this.inscricoesView = inscricoesView;

        inscreverAtleta.setupBackButton(this::onBack);
        inscreverAtleta.setupConcluirButton(this::inscreverAtleta);

        inscreverEmProva.setupBackButton(this::onBack);
        inscreverEmProva.setupConcluirButton(this::inscreverAtletas);

        inscricoesView.setupBackButton(this::onBack);
    }

    private void onBack() {
        viewController.onBackRequested();
    }

    @Override
    public void mostrarInscreverAtleta(Atleta atleta) {
        inscreverAtleta.setAtleta(atleta);
        viewController.displayView(inscreverAtleta);
    }

    @Override
    public void mostrarInscreverEmProvaAtleta(Prova prova) {
        inscreverEmProva.setProva(prova);
        viewController.displayView(inscreverEmProva);
    }

    @Override
    public void mostrarInscricoes() {
        viewController.displayView(inscricoesView);
    }

    private void inscreverAtleta(Atleta atleta, Map.Entry<List<Prova>, List<Prova>> dados) {
        inscreverAtleta(atleta, dados.getKey(), dados.getValue());
    }

    @Override
    public void inscreverAtleta(Atleta atleta, List<Prova> inscrever, List<Prova> desinscrever) {
        if (!inscrever.isEmpty() || !desinscrever.isEmpty()) {
            if (databaseConnector.inscreverAtleta(atleta, inscrever, desinscrever))
                viewController.mostrarMensagem("Inscrições concluidas com sucesso.");
            else
                viewController.mostrarAviso("Falha ao guardar inscrições.");
        }

        viewController.onBackRequested();
    }

    private void inscreverAtletas(Prova prova, Map.Entry<List<Atleta>, List<Atleta>> dados) {
        inscreverAtletas(prova, dados.getKey(), dados.getValue());
    }

    @Override
    public void inscreverAtletas(Prova prova, List<Atleta> inscrever, List<Atleta> desinscrever) {
        if (!inscrever.isEmpty() || !desinscrever.isEmpty()) {
            if (databaseConnector.inscreverAtletasEmProva(prova, inscrever, desinscrever))
                viewController.mostrarMensagem("Inscrições concluidas com sucesso.");
            else
                viewController.mostrarAviso("Falha ao guardar inscrições.");
        }

        viewController.onBackRequested();
    }
}
