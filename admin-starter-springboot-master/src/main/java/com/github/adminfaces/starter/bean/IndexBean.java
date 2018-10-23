package com.github.adminfaces.starter.bean;

import java.io.Serializable;

import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("view")
public class IndexBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private ScheduleModel eventModel = new DefaultScheduleModel();

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}

}