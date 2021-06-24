package model;

import API.Sexo;

import java.util.Date;

public class Atleta extends UniqueId {
    private String nome;
    private String pais;
    private Sexo sexo;
    private Date dataDeNascimento;
    private String contacto;

    public Atleta() {
    }

    public Atleta(String nome, String pais, Sexo sexo, Date dataDeNascimento, String contacto) {
        this.nome = nome;
        this.pais = pais;
        this.sexo = sexo;
        this.dataDeNascimento = dataDeNascimento;
        this.contacto = contacto;
    }

    public String getNome() {
        return nome;
    }

    public String getPais() {
        return pais;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public Date getDataDeNascimento() {
        return dataDeNascimento;
    }

    public long getDataDeNascimentoTime() {
        return dataDeNascimento.getTime();
    }

    public String getContacto() {
        return contacto;
    }

    @Override
    public String toString() {
        return nome + " (" + pais + ")";
    }
}
