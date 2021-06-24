package model;

import API.TipoDeContagem;

public class Modalidade extends UniqueId {
    private String nome;
    private TipoDeContagem tipoDeContagem;

    public Modalidade() {
    }

    protected Modalidade(int id, String nome) {
        super(id);
        this.nome = nome;
    }

    public Modalidade(String nome, TipoDeContagem tipoDeContagem) {
        this.nome = nome;
        this.tipoDeContagem = tipoDeContagem;
    }

    public String getNome() {
        return nome;
    }

    public TipoDeContagem getTipoDeContagem() {
        return tipoDeContagem;
    }

    @Override
    public String toString() {
        return nome;
    }
}
