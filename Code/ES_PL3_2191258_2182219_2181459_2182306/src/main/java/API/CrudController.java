package API;

public interface CrudController<T> {
    void create();

    void index();

    void store(T data);
}
