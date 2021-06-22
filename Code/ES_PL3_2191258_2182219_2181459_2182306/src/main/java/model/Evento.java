package model;

import java.util.Calendar;
import java.util.Date;

public class Evento extends UniqueId {
    @SuppressWarnings("unused")
    private String nome;
    private Date inicio;
    private Date fim;
    private String pais;
    private String local;

    public Evento() {
    }

    protected Evento(int id, String nome) {
        super(id);
        this.nome = nome;
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

    public Calendar getInicioCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(inicio);
        return cal;
    }

    public long getInicioTime() {
        return inicio.getTime();
    }

    public Date getFim() {
        return fim;
    }

    public Calendar getFimCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fim);
        return cal;
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
        int anoDeInicio = getInicioCalendar().get(Calendar.YEAR);
        int anoDeFim = getFimCalendar().get(Calendar.YEAR);

        StringBuilder builder = new StringBuilder(nome);
        builder.append(" (");
        builder.append(anoDeInicio);
        if (anoDeInicio != anoDeFim) {
            builder.append(" - ");
            builder.append(anoDeFim);
        }
        builder.append(") | ");
        builder.append(pais);
        builder.append(" (");
        builder.append(local);
        builder.append(")");
        return builder.toString();
    }
}