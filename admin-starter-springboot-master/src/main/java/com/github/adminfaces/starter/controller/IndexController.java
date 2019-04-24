package com.github.adminfaces.starter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.adminfaces.starter.bean.UsuarioLogadoBean;
import com.github.adminfaces.starter.model.Permissao;
import com.github.adminfaces.starter.model.Usuario;

@Controller
@RequestMapping
public class IndexController {
	private final String PERMISSAO_ADM = "ROLE_ADMIN";
	private final String PERMISSAO_ATENDENTE = "ROLE_ATENDENTE";

	@GetMapping("/index")
	public String home(@AuthenticationPrincipal Usuario logado) {
		if (logado.hasRole(PERMISSAO_ADM,logado)) {
			return "redirect:index.jsf";
		}
		if (logado.hasRole(PERMISSAO_ATENDENTE,logado)) {
			return "redirect:index.jsf";
		}

		return "redirect:indexcliente.jsf";

	}

}
