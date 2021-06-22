package API;

public interface CrudController<T> {
    //todo limitar listas
    //todo permitir enter para guardar

    void create();

    void index();

    void store(T data);

    void mostrarAviso(String mensagem);
}
