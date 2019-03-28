package com.github.adminfaces.starter.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "servico")
public class Servico implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Integer id;

    @Column(length = 100,nullable = false)
    private String nome;

    @Column(nullable = false)
    private LocalTime tempo;

    @Column(nullable = false)
    private Double valor;

    @Column(length = 200,nullable = true)
    private String descricao;

}
