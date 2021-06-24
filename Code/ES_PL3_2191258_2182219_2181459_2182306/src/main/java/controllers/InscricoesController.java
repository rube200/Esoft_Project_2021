package controllers;

import API.DatabaseConnector;
import com.google.inject.Inject;
import model.Atleta;
import model.Prova;
import views.inscricoes.InscreverAtleta;

import java.util.List;
import java.util.Map;

public class InscricoesController {
    private final InscreverAtleta inscreverAtleta;

    @Inject
    private ViewController viewController;

    @Inject
    public InscricoesController(InscreverAtleta inscreverAtleta) {
        this.inscreverAtleta = inscreverAtleta;
        inscreverAtleta.setupBackButton(this::onBack);
        inscreverAtleta.setupConcluirButton(this::inscreverAtletas);
    }

    private void onBack() {
        viewController.onBackRequested();
    }

    public void mostrarInscreverAtleta(Prova prova) {
        inscreverAtleta.setProva(prova);
        viewController.displayView(inscreverAtleta);
    }

    @Inject
    private DatabaseConnector databaseConnector;

    public void inscreverAtletas(Prova prova, Map.Entry<List<Atleta>, List<Atleta>> dados) {
        inscreverAtletas(prova, dados.getKey(), dados.getValue());
    }

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
