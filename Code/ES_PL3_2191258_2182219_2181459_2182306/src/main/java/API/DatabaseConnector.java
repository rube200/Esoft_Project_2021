package API;

import model.*;

import java.util.Collection;
import java.util.List;

public interface DatabaseConnector {
    Collection<Atleta> getAtletasInscritos(Prova prova);

    Collection<Atleta> getAtletasNaoInscritos(Prova prova);

    boolean delete(Atleta atleta);

    Collection<Atleta> getAtletas();

    boolean store(Atleta atleta);

    boolean update(Atleta atleta);

    boolean delete(Evento evento);

    Evento getEvento(Prova prova);

    Collection<Evento> getEventos();

    Collection<Evento> getEventosAtuais();

    Collection<Evento> getEventoAtuaisOuAnteriores();

    Collection<Evento> getEventoAtuaisOuFuturos();

    boolean store(Evento evento);

    boolean update(Evento evento);

    boolean delete(Prova prova);

    Collection<Prova> getProvas();

    Collection<Prova> getProvas(Evento evento);

    Collection<Prova> getProvasInscrito(Evento evento, Atleta atleta);

    Collection<Prova> getProvasNaoInscrito(Evento evento, Atleta atleta);

    Collection<Prova> getProvasAtuais();

    boolean store(Prova prova);

    boolean update(Prova prova);

    boolean delete(Modalidade modalidade);

    Collection<Modalidade> getModalidades();

    boolean store(Modalidade modalidade);

    boolean update(Modalidade modalidade);

    boolean inscreverAtleta(Atleta atleta, List<Prova> inscrever, List<Prova> desinscrever);

    boolean inscreverAtletasEmProva(Prova prova, List<Atleta> inscrever, List<Atleta> desinscrever);

    Collection<Medalhas> getMedalhados();

    Collection<Medalhas> getMedalhados(Evento evento);
}
