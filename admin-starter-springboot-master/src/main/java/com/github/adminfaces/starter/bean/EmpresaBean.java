package com.github.adminfaces.starter.bean;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.Empresa;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.EmpresaRepository;

@Component
@Scope("view")
public class EmpresaBean extends AbastractFormBean<Empresa, EmpresaRepository> {

	@Autowired
	private UsuarioLogadoBean usuarioLogadoBean;
	@Autowired
	private EmpresaRepository empresaRepository;

	@Autowired

	public EmpresaBean() {
		super(Empresa.class);
	}

	@Override
	public void init() throws InstantiationException, IllegalAccessException {
		verificaPermissao();
		if (!empresaRepository.findAll().isEmpty()) {
			setId(empresaRepository.findAll().get(0).getId());
		}

		super.init();
	}

	public void atualizar() {
		getRepository().save(getObjeto());
		addDetailMessage("Cadastro Atualizado com sucesso!");
		Faces.getExternalContext().getFlash().setKeepMessages(true);
	}
	
	private void verificaPermissao() {
		if (Usuario.hasRole("ROLE_ADMIN", usuarioLogadoBean.getUsuario())) {
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("indexcliente.jsf");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}