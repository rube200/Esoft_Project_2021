package model;

import API.Genero;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "Atletas")
public class Atleta {
    @Id
    @GeneratedValue
    private int id;
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false)
    private Genero genero;
    @Column(nullable = false)
    private Date nascimento;
    @Column(nullable = false)
    private String pais;
    @OneToMany
    private Collection<Inscricao> inscricoes;
    @OneToMany
    private Collection<Recorde> recordes;
}
