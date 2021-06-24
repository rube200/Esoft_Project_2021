package model;

import API.Sexo;

import java.util.Date;

public class Prova extends UniqueId {
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

    public Prova(int eventoId, int modalidadeId, Sexo sexo, int minimos, byte atletasPorProva, Date dataDaProva) {
        this.eventoId = eventoId;
        this.modalidadeId = modalidadeId;
        this.sexo = sexo;
        this.minimos = minimos;
        this.atletasPorProva = atletasPorProva;
        this.dataDaProva = dataDaProva;
    }

    public int getEventoId() {
        return eventoId;
    }

    public int getModalidadeId() {
        return modalidadeId;
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