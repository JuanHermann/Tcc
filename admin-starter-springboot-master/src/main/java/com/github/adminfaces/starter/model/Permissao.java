package com.github.adminfaces.starter.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Permissao implements GrantedAuthority {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 50, nullable = false)
	private String nome;

	@Override
	public String getAuthority() {
		return this.nome;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "usuario_permissao", joinColumns = @JoinColumn(name = "permissao_id"), inverseJoinColumns = @JoinColumn(name = "usuario_id"))
	private List<Usuario> usuarios = new ArrayList<>();


	
}
