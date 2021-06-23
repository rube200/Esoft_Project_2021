package API;

public interface CrudController<T> {
    //todo limitar listas
    //todo permitir enter para guardar

    void destroy(T data);

    T create();

    void edit(T data);

    void index();

    void update(T data);

    void store(T data);

    void mostrarAviso(String mensagem);
}
