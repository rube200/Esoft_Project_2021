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
     * Obtem os dados sobre provas
     *
     * @return uma coleção com as provas ou null se falhar
     */
    Collection<Prova> getProvas();
}
