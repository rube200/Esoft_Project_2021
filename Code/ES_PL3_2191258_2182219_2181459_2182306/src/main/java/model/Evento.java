package model;

import javax.persistence.*;
import java.sql.Date;
import java.util.Collection;

@Entity
@Table(name = "Eventos")
public class Evento {
    @Id
    @GeneratedValue
    private int id;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false)
    private Date inicio;
    @Column(nullable = false)
    private Date fim;
    @Column(nullable = false)
    private String local;
    @Column(nullable = false)
    private String pais;
    @OneToMany
    private Collection<Prova> provas;
}
