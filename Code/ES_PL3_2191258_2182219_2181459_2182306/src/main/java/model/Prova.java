package model;

import API.Sexo;

import java.util.Date;

public class Prova {
    private int id;
    private int eventoId;
    private int modalidadeId;
    private Sexo sexo;
    private int minimos;
    private byte atletasPorProva;

    public Prova() {

    }

    public Prova(int eventoId, int modalidadeId, Sexo sexo, int minimos, byte atletasPorProva) {
        this.eventoId = eventoId;
        this.modalidadeId = modalidadeId;
        this.sexo = sexo;
        this.minimos = minimos;
        this.atletasPorProva = atletasPorProva;
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

    @Override
    public String toString() {
        //todo
        return super.toString();
    }
}