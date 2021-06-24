package model;

public class SemDadosModalidades extends Modalidade {
    public static final int SEM_DADOS_ID = -3;

    public SemDadosModalidades() {
        super(SEM_DADOS_ID, "Sem dados sobre modalidades");
    }

    @Override
    public String toString() {
        return getNome();
    }
}
