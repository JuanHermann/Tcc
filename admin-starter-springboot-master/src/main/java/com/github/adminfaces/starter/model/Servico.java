package com.github.adminfaces.starter.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "servico")
public class Servico implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 100, nullable = false)
	private String nome;

	@Column(nullable = false)
	private LocalTime tempo;

	@Column(nullable = false)
	private Double valor;

	@Column(length = 200, nullable = true)
	private String descricao;

	private boolean ativo;

	@Override
	public String toString() {
		return nome;
	}

	
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Servico other = (Servico) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
