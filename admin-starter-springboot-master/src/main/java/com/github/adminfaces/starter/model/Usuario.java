package com.github.adminfaces.starter.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.Getter;
import lombok.Setter;



@Entity
@Getter
@Setter
@Table(name = "usuario")
public class Usuario implements UserDetails {
	
	private static final long serialVersionUID = 1L;
	public static final String ADMIN_EMAIL = "admin@admin.com";
	public static final Integer COD_ADMIN = 1;
	private static final BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder(10);
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 100, nullable = false)
	private String nome;

	@Column(length = 100, nullable = false)
	private String email;

	@Column(length = 100, nullable = false)
	private String senha;

	@Column(length = 100, nullable = false)
	private String telefone;

	@Column(nullable = false)
	private boolean ativo; // futuro cadastro de um empregado que foi despedido

	@ManyToMany( fetch = FetchType.EAGER)
	@JoinTable(name = "usuario_permissao", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "permissao_id"))
	private List<Permissao> permissoes;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> auto = new ArrayList<>();
		auto.addAll(getPermissoes());

		return auto;
	}
	
	public static boolean hasRole(String role,Usuario usuario) {
		for(Permissao p : usuario.getPermissoes()) {
			if(p.getNome().equals(role)) {
				return true;
			}
		}
		return false;
	}
	
	public void addPermissao(Permissao permissao) {
		if (permissoes == null) {
			permissoes = new ArrayList<>();
		}
		permissoes.add(permissao);
	}
	
	public void removePermissao(Permissao permissao) {
		if (permissoes != null) {
			permissoes.remove(permissao);
		}
	}
	
	public List<Usuario> filtraPorNovosCadastros(List<Usuario> listalGeral){
		List<Usuario> lista = new ArrayList<>();
		boolean tem;
		for(Usuario usuario: listalGeral) {
			tem = false;
			for(Permissao permissao: usuario.getPermissoes()) {
				if(permissao.getNome().equals("ROLE_CADASTRADO")) {
					tem = true;
				}
			}
			if(tem==true) {
				lista.add(usuario);
			}
		}
		
		return lista;
	}
	
	public List<Usuario> filtraPorClientes(List<Usuario> listalGeral){
		List<Usuario> lista = new ArrayList<>();
		boolean tem;
		for(Usuario usuario: listalGeral) {
			tem = false;
			for(Permissao permissao: usuario.getPermissoes()) {
				if(permissao.getNome().equals("ROLE_CLIENTE")) {
					tem = true;
				}
			}
			if(tem==true) {
				lista.add(usuario);
			}
		}
		
		return lista;
	}
	
	public static List<Usuario> filtraPorRole(List<Usuario> listalGeral,String nomeRole){
		List<Usuario> lista = new ArrayList<>();
		boolean tem;
		for(Usuario usuario: listalGeral) {
			tem = false;
			for(Permissao permissao: usuario.getPermissoes()) {
				if(permissao.getNome().equals(nomeRole)) {
					tem = true;
				}
			}
			if(tem==true) {
				lista.add(usuario);
			}
		}
		
		return lista;
	}

	@Override
	public String getPassword() {
		return senha;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public boolean hasNome() {
		return nome != null && !"".equals(nome.trim());
	}
	
	

}
