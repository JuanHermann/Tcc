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
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name= "servico_id",referencedColumnName="id")
    private Servico servico;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Servico getServico() {
		return servico;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}


    
    
}
