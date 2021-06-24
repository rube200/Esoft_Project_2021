package model;

public class TodosDadosEventos extends Evento {
    public static final int TODOS_EVENTOS_ID = -4;

    public TodosDadosEventos() {
        super(TODOS_EVENTOS_ID, "Todos os eventos");
    }

    @Override
    public String toString() {
        return getNome();
    }
}