package model;

public class Medalhas {
    private String nome;
    private int ouro;
    private int prata;
    private int bronze;

    public Medalhas() {
    }

    public Medalhas(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public int getOuro() {
        return ouro;
    }

    public int getPrata() {
        return prata;
    }

    public int getBronze() {
        return bronze;
    }

    public void incrementOuro() {
        ouro++;
    }

    public void incrementPrata() {
        prata++;
    }

    public void incrementBronze() {
        bronze++;
    }

    @Override
    public int hashCode() {
        return nome.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Medalhas medalhas && nome.equals(medalhas.nome);
    }
}
