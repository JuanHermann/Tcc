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
    
    

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Usuario getCliente() {
		return cliente;
	}

	public void setCliente(Usuario cliente) {
		this.cliente = cliente;
	}

	public UsuarioServico getUsuarioServico() {
		return UsuarioServico;
	}

	public void setUsuarioServico(UsuarioServico usuarioServico) {
		UsuarioServico = usuarioServico;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Time getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(Time horaInicio) {
		this.horaInicio = horaInicio;
	}

	public Time getHoraTermino() {
		return horaTermino;
	}

	public void setHoraTermino(Time horaTermino) {
		this.horaTermino = horaTermino;
	}
    
    
}
