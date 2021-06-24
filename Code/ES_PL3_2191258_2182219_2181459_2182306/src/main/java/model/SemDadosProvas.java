package model;

public class SemDadosProvas extends Prova {
    public static final int SEM_DADOS_ID = -3;

    public SemDadosProvas() {
        super(SEM_DADOS_ID, "Sem dados sobre provas");
    }

    @Override
    public String toString() {
        return getNome();
    }
}
