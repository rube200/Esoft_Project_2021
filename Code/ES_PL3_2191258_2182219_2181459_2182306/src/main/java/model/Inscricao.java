package model;

import javax.persistence.*;

@Entity
@Table(name = "Inscricoes")
public class Inscricao {
    @Id
    @GeneratedValue
    private int id;
    @JoinColumn(nullable = false)
    @ManyToOne
    private Atleta atleta;
    @JoinColumn(nullable = false)
    @ManyToOne
    private Prova prova;

}
