package API;

import model.Evento;
import model.Modalidade;
import model.Prova;

import java.util.Collection;

public interface DatabaseConnector {
    boolean delete(Evento evento);

    Collection<Evento> getEventos();

    Collection<Evento> getEventosAtuais();

    Collection<Evento> getEventoAtuaisOuFuturos();

    boolean store(Evento evento);

    boolean update(Evento evento);

    boolean delete(Prova prova);

    Collection<Prova> getProvas();

    Collection<Prova> getProvasAtuais();

    boolean store(Prova prova);

    boolean update(Prova prova);

    boolean delete(Modalidade modalidade);

    Collection<Modalidade> getModalidades();

    boolean store(Modalidade modalidade);

    boolean update(Modalidade modalidade);
}
