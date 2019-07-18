package com.github.adminfaces.starter.util;


import org.omnifaces.util.Messages;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import java.io.Serializable;
import javax.inject.Named;

@Named
@ApplicationScoped
public class Utils implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void addDetailMessage(String message) {
		addDetailMessage(message, null);
	}

	public static void addErrorMessage(String message) {
		addDetailMessage(message, FacesMessage.SEVERITY_ERROR);
	}

	public static void addDetailMessage(String message, FacesMessage.Severity severity) {

		FacesMessage facesMessage = Messages.create("").detail(message).get();
		if (severity != null && severity != FacesMessage.SEVERITY_INFO) {
			facesMessage.setSeverity(severity);
		}
		Messages.add(null, facesMessage);
	}

}
