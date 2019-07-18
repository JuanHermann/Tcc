package com.github.adminfaces.starter.controller;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
public class LoginController {

	@GetMapping("/login")
	public void loginIncorreto(@RequestParam("error") String error) {
		if(error.equals("bad-credentials")) 
			System.out.println(error);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Email não cadastrado em nosso sistema."));


	}

}
