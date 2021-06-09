package model;

import API.Sexo;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "Provas")
public class Prova {
    @Id
    @GeneratedValue
    private int id;
    @JoinColumn(nullable = false)
    @ManyToOne
    private Evento evento;
    @JoinColumn(nullable = false)
    @ManyToOne
    private Modalidade modalidade;
    @Column(nullable = false)
    private byte atletasPorProva;
    @Column(nullable = false)
    private short minimos;
    @Column(nullable = false)
    private Sexo sexo;
    @OneToMany
    private Collection<Etapa> etapas;
    @OneToMany
    private Collection<Inscricao> inscricoes;
}
