package API;

import model.Atleta;
import model.Prova;

import java.util.List;

public interface InscricoesController {
    void mostrarInscreverAtleta(Atleta atleta);

    void mostrarInscreverEmProvaAtleta(Prova prova);

    void mostrarInscricoes();

    void inscreverAtleta(Atleta atleta, List<Prova> inscrever, List<Prova> desinscrever);

    void inscreverAtletas(Prova prova, List<Atleta> inscrever, List<Atleta> desinscrever);
}
