package model;

import API.Sexo;

import java.util.Date;

public class Prova extends UniqueId {
    String diaDeCompeticao;
    @SuppressWarnings("unused")
    private String nome;
    private int eventoId;
    private int modalidadeId;
    private Sexo sexo;
    private int minimos;
    private byte atletasPorProva;
    private Date dataDaProva;

    public Prova() {
    }

    protected Prova(int id, String nome) {
        super(id);
        this.nome = nome;
    }

    public Prova(int eventoId, int modalidadeId, String diaDeCompeticao, Sexo sexo, int minimos, byte atletasPorProva, Date dataDaProva) {
        this.eventoId = eventoId;
        this.modalidadeId = modalidadeId;
        this.sexo = sexo;
        this.diaDeCompeticao = diaDeCompeticao;
        this.minimos = minimos;
        this.atletasPorProva = atletasPorProva;
        this.dataDaProva = dataDaProva;
    }

    public String getNome() {
        return nome;
    }

    public int getEventoId() {
        return eventoId;
    }

    public int getModalidadeId() {
        return modalidadeId;
    }

    public String getDiaDeCompeticao() {
        return diaDeCompeticao;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public int getMinimos() {
        return minimos;
    }

    public byte getAtletasPorProva() {
        return atletasPorProva;
    }

    public Date getDataDaProva() {
        return dataDaProva;
    }

    public long getDataDaProvaTime() {
        return dataDaProva.getTime();
    }

    @Override
    public String toString() {
        return nome + " - " + "date" + " (" + "hora" + ")";
    }
}