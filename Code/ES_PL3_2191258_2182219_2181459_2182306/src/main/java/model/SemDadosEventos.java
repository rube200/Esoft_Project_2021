package model;

public class SemDadosEventos extends Evento {
    public static final int SEM_DADOS_ID = -3;

    public SemDadosEventos() {
        super(SEM_DADOS_ID, "Sem dados sobre eventos");
    }

    @Override
    public String toString() {
        return getNome();
    }
}

