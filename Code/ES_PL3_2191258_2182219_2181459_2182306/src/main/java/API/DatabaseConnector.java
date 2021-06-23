package API;

import model.Evento;
import model.Modalidade;
import model.Prova;

import java.util.Collection;

public interface DatabaseConnector {
    Collection<Evento> getEventos();
    Collection<Evento> getEventosAtuais();
    Collection<Evento> getEventoAtuaisOuFuturos();
    boolean store(Evento evento);
    boolean update(Evento evento);

    Collection<Prova> getProvas();
    Collection<Prova> getProvasAtuais();
    boolean store(Prova prova);
    boolean update(Prova prova);

    Collection<Modalidade> getModalidades();
    boolean store(Modalidade modalidade);
    boolean update(Modalidade modalidade);
}
