package API;

public interface CrudController<T> {
    T create();

    void destroy(T data);

    void edit(T data);

    void index();

    void update(T data);

    void store(T data);

    void mostrarAviso(String mensagem);
}
