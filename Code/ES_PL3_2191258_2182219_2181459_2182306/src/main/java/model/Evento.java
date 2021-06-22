package model;

import java.util.Date;

public class Evento {
    private int id;
    private String nome;
    private Date inicio;
    private Date fim;
    private String pais;
    private String local;

    public Evento() {
    }

    public Evento(String nome, Date inicio, Date fim, String pais, String local) {
        this.nome = nome;
        this.inicio = inicio;
        this.fim = fim;
        this.local = local;
        this.pais = pais;
    }

    public String getNome() {
        return nome;
    }

    public Date getInicio() {
        return inicio;
    }

    public long getInicioTime() {
        return inicio.getTime();
    }

    public Date getFim() {
        return fim;
    }

    public long getFimTime() {
        return fim.getTime();
    }

    public String getPais() {
        return pais;
    }

    public String getLocal() {
        return local;
    }

    @Override
    public String toString() {
        return id + " inicio " + inicio + " fim " + fim;
        //todo
    }
}