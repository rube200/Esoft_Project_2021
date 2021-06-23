package API;

public interface CrudController<T> {
    //todo limitar listas
    //todo permitir enter para guardar

    T create();

    void edit(T data);

    void index();

    void store(T data);

    void mostrarAviso(String mensagem);
}
