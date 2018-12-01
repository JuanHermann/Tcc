package com.github.adminfaces.starter.model;


import java.util.Collection;
import java.util.Collections;

import javax.persistence.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "usuario")
public class Usuario implements UserDetails{

	private static final long serialVersionUID = 1L;
	public static final String ADMIN_EMAIL = "admin";
	public static final Integer COD_ADMIN = 1;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Integer id;

    @Column(length = 100,nullable = false)
    private String  nome;

    @Column(length = 100,nullable = false)
    private String  email;

    @Column(length = 100,nullable = false)
    private String  senha;

    @Column(length = 100,nullable = false)
    private String  telefone;

    @Column(length = 100,nullable = false)
    private Integer  tipo;// 1-adm 2-funcionario 3-atendente 4-cliente

    @Column(nullable = false)
    private boolean  aceito;// novo cadastro de usuario

    @Column(nullable = false)
    private boolean  ativo; // futuro cadastro de um empregado que foi despedido
    private String roleName;
	
	public Usuario() {
		roleName = "ROLE_USER";
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(() -> roleName);
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
	
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public boolean isAceito() {
        return aceito;
    }

    public void setAceito(boolean aceito) {
        this.aceito = aceito;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
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
