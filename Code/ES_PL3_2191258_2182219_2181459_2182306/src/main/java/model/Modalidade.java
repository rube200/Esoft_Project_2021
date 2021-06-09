package model;

import javax.persistence.*;

@Entity
@Table(name = "Modalidades")
public class Modalidade {
    @Id
    @GeneratedValue
    private int id;
    @Column(nullable = false)
    private String nome;
}
