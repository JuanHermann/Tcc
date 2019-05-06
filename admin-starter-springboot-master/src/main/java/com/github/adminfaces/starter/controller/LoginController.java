package com.github.adminfaces.starter.controller;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class LoginController {

	@GetMapping("/login")
	public String loginIncorreto() {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Email não cadastrado em nosso sistema."));
		return "redirect:login.jsf";

	}

}
