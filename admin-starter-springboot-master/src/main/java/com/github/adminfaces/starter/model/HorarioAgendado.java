package com.github.adminfaces.starter.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

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
    private Date data;

    @Column(nullable = false)
    private Time horaInicio;

    @Column(nullable = false)
    private Time horaTermino;
    
   
}
