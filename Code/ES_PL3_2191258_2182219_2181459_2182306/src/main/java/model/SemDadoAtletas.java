package model;

public class SemDadoAtletas extends Atleta {
    public static final int SEM_DADOS_ID = -3;

    public SemDadoAtletas() {
        super(SEM_DADOS_ID, "Sem dados sobre atletas");
    }

    @Override
    public String toString() {
        return getNome();
    }
}