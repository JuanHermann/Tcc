package com.github.adminfaces.starter.model;

import javax.persistence.*;

@Entity
@Table(name = "usuario_servico")
public class UsuarioServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @ManyToOne
    @JoinColumn(name= "usuario_id",referencedColumnName="id")
    private Usuario codUsuario;

    @ManyToOne
    @JoinColumn(name= "servico_id",referencedColumnName="id")
    private Servico codServico;
}
