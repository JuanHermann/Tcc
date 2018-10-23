package com.github.adminfaces.starter.model;

import javax.persistence.*;
import java.sql.Time;

@Entity
@Table(name = "empresa")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @Column(length = 100,nullable = false)
    private String nome;

    @Column(nullable = false)
    private Time horaAbertura;

    @Column(nullable = false)
    private Time horaFechamento;

    @Column(nullable = false)
    private Time horaIntervalor;

    @Column(nullable = false)
    private Time Intervalor;



}
