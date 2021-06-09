package model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Etapas")
public class Etapa {
    @Id
    @GeneratedValue
    private int id;
    @JoinColumn(nullable = false)
    @ManyToOne
    private Prova prova;
    @Column(nullable = false)
    private String Nome;
    @Column(nullable = false)
    private Date data;

}
