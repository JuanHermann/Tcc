package com.github.adminfaces.starter.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "horario_agendado")
public class HorarioAgendado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @ManyToOne
    @JoinColumn(name= "usuario_id",referencedColumnName="id")
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name= "usuario_servico_id",referencedColumnName="id")
    private UsuarioServico UsuarioServico;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaTermino;
    
   
}
