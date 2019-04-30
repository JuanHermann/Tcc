package com.github.adminfaces.starter.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "empresa")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Integer id;

    @Column(length = 100,nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalTime horaAbertura;

    @Column(nullable = false)
    private LocalTime horaFechamento;

    @Column(nullable = false)
    private LocalTime inicioIntervalo;

    @Column(nullable = false)
    private LocalTime finalIntervalo;
    
    @Column(nullable = false)
    private LocalTime tempoCancelamento;

    @Column(nullable = false)
    private LocalTime tempoIntervalo;


}
