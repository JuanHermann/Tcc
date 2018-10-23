package com.github.adminfaces.starter.model;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "horario_agendado")
public class HorarioAgendado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @ManyToOne
    @JoinColumn(name= "usuario_id",referencedColumnName="id")
    private Usuario codUsuario;

    @ManyToOne
    @JoinColumn(name= "servico_id",referencedColumnName="id")
    private Servico codServico;

    @ManyToOne
    @JoinColumn(name= "usuario_servico_id",referencedColumnName="id")
    private UsuarioServico codUsuarioServico;

    @Column(nullable = false)
    private Date data;

    @Column(nullable = false)
    private Time horaInicio;

    @Column(nullable = false)
    private Time horaTermino;
}
