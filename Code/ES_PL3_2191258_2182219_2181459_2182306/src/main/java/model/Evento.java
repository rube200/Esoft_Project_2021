package model;

import java.sql.Date;

public class Evento {
    private int id;
    private String nome;
    private Date inicio;
    private Date fim;
    private String local;
    private String pais;

    public Evento() {
    }

    @Override
    public String toString() {
        //todo
        return super.toString();
    }
}