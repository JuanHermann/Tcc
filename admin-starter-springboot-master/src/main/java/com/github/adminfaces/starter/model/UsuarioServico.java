package com.github.adminfaces.starter.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "usuario_servico")
public class UsuarioServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @ManyToOne
    @JoinColumn(nullable=false, name= "usuario_id",referencedColumnName="id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(nullable=false, name= "servico_id",referencedColumnName="id")
    private Servico servico;
    
    private boolean ativo;    
}
