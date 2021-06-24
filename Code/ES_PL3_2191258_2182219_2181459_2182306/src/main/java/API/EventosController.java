package API;

import model.Evento;

public interface EventosController extends CrudController<Evento> {
    void mostrarPrograma();
}
