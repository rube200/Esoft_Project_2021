package model;

import javax.persistence.*;

@Entity
@Table(name = "Recordes")
public class Recorde {
    @Id
    @GeneratedValue
    private int id;
    @JoinColumn(nullable = false)
    @ManyToOne
    private Atleta atleta;
    @JoinColumn(nullable = false)
    @ManyToOne
    private Modalidade modalidade;
}
