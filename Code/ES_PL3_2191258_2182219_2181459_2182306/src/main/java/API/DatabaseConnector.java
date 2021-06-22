package API;

import model.Evento;
import model.Prova;

import java.util.Collection;

public interface DatabaseConnector {
    Collection<Evento> getEventos();
    Collection<Evento> getEventos(boolean decorrer);
    boolean store(Evento evento);

    Collection<Prova> getProvas();
    Collection<Prova> getProvas(boolean decorrer);
    boolean store(Prova prova);
}
