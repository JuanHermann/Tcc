package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.Empresa;
import com.github.adminfaces.starter.repository.EmpresaRepository;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

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
		usuarioLogadoBean.getUsuario().hasRole("ROLE_ADMIN", usuarioLogadoBean.getUsuario());
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

}