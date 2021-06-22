package API;

import model.Evento;
import model.Prova;

import java.util.Collection;

public interface DatabaseConnector {
    /**
     * Obtem os dados sobre eventos
     *
     * @return uma coleção com os eventos ou null se falhar
     */
    Collection<Evento> getEventos();

    /**
     * Obtem os dados sobre eventos
     *
     * @param decorrer apenas eventos a decorrer ou todos
     * @return uma coleção com os eventos ou null se falhar
     */
    Collection<Evento> getEventos(boolean decorrer);

    /**
     * Obtem os dados sobre provas
     *
     * @return uma coleção com as provas ou null se falhar
     */
    Collection<Prova> getProvas();

    /**
     * Obtem os dados sobre provas
     *
     * @param decorrer apenas provas a decorrer ou todos
     * @return uma coleção com os provas ou null se falhar
     */
    Collection<Prova> getProvas(boolean decorrer);

    boolean store(Evento evento);

    boolean store(Prova prova);
}
