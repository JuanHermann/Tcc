package com.github.adminfaces.starter.bean;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.HorarioAgendado;
import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.repository.HorarioAgendadoRepository;
import com.github.adminfaces.starter.repository.ServicoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;

@Component
@Scope("view")
public class IndexBean extends AbastractFormBean<HorarioAgendado, HorarioAgendadoRepository> {

	private ScheduleModel eventModel = new DefaultScheduleModel();

	private ScheduleModel lazyEventModel;

	private ScheduleEvent event = new DefaultScheduleEvent();

	private String tipo = "servico";
	private Time horarios;

	@Autowired
	private UsuarioRepository usuarioRepository;
	private List<Usuario> funcionarios;
	private Set<Usuario> setFuncionarios;
	

	@Autowired
	private ServicoRepository servicoRepository;
	private List<Servico> servicos;

	private List<Servico> servicosSelecionados;

	@Autowired
	private UsuarioServicoRepository usuarioServicoRepository;
	private List<UsuarioServico> usuarioServicos;

	public IndexBean() {
		super(HorarioAgendado.class);

	}

	@PostConstruct
	public void init() {
		servicos = servicoRepository.findAll();
		funcionarios = new ArrayList<>();
		setFuncionarios = new HashSet<>();
		eventModel = new DefaultScheduleModel();
		eventModel.addEvent(new DefaultScheduleEvent("Champions League Match", previousDay8Pm(), previousDay11Pm()));
		eventModel.addEvent(new DefaultScheduleEvent("Birthday Party", today1Pm(), today6Pm()));
		eventModel.addEvent(new DefaultScheduleEvent("Breakfast at Tiffanys", nextDay9Am(), nextDay11Am()));
		eventModel.addEvent(new DefaultScheduleEvent("Plant the new garden stuff", theDayAfter3Pm(), fourDaysLater3pm()));

		lazyEventModel = new LazyScheduleModel() {

			public void loadEvents(Date start, Date end) {
				Date random = getRandomDate(start);
				addEvent(new DefaultScheduleEvent("Lazy Event 1", random, random));

				random = getRandomDate(start);
				addEvent(new DefaultScheduleEvent("Lazy Event 2", random, random));
			}
		};
	}

	public void buscarFuncionarios() {
		System.out.println("passou");
		if (servicosSelecionados != null) {
			for (Servico servico : servicosSelecionados) {
				usuarioServicos = usuarioServicoRepository.findByServicoOrderByUsuario(servico);
				for (UsuarioServico usuarioServico : usuarioServicos) {
					setFuncionarios.add(usuarioServico.getUsuario());
//					System.out.println("top");
				}
			}
		}
	}

	public boolean mostrarForm() {
//		System.out.println(tipo);
//		System.out.println("servico".equalsIgnoreCase(tipo));
		return "servico".equalsIgnoreCase(tipo);

	}

	public void salvarAgendamento() {

	}

	public Date getRandomDate(Date base) {
		Calendar date = Calendar.getInstance();
		date.setTime(base);
		date.add(Calendar.DATE, ((int) (Math.random() * 30)) + 1); // set random day of month

		return date.getTime();
	}

	public Date getInitialDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY, calendar.get(Calendar.DATE), 0, 0, 0);

		return calendar.getTime();
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}

	private Calendar today() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);

		return calendar;
	}

	private Date previousDay8Pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) - 1);
		t.set(Calendar.HOUR, 8);

		return t.getTime();
	}

	private Date previousDay11Pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) - 1);
		t.set(Calendar.HOUR, 11);

		return t.getTime();
	}

	private Date today1Pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.HOUR, 1);

		return t.getTime();
	}

	private Date theDayAfter3Pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.DATE, t.get(Calendar.DATE) + 2);
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.HOUR, 3);

		return t.getTime();
	}

	private Date today6Pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.HOUR, 6);

		return t.getTime();
	}

	private Date nextDay9Am() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.AM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
		t.set(Calendar.HOUR, 9);

		return t.getTime();
	}

	private Date nextDay11Am() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.AM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) + 1);
		t.set(Calendar.HOUR, 11);

		return t.getTime();
	}

	private Date fourDaysLater3pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.DATE, t.get(Calendar.DATE) + 4);
		t.set(Calendar.HOUR, 3);

		return t.getTime();
	}

	public ScheduleEvent getEvent() {
		return event;
	}

	public void setEvent(ScheduleEvent event) {
		this.event = event;
	}

	public void addEvent() {
		if (event.getId() == null)
			eventModel.addEvent(event);
		else
			eventModel.updateEvent(event);

		event = new DefaultScheduleEvent();
	}

	public void onEventSelect(SelectEvent selectEvent) {
		event = (ScheduleEvent) selectEvent.getObject();
	}

	public void onDateSelect(SelectEvent selectEvent) {
		event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
	}

	public void onEventMove(ScheduleEntryMoveEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved",
				"Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

		addMessage(message);
	}

	public void onEventResize(ScheduleEntryResizeEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized",
				"Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

		addMessage(message);
	}

	private void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public List<Usuario> getFuncionarios() {
		return funcionarios;
	}

	public void setFuncionarios(List<Usuario> funcionarios) {
		this.funcionarios = funcionarios;
	}

	public List<Servico> getServicos() {
		return servicos;
	}

	public void setServicos(List<Servico> servicos) {
		this.servicos = servicos;
	}

	public List<Servico> getServicosSelecionados() {
		return servicosSelecionados;
	}

	public void setServicosSelecionados(List<Servico> servicosSelecionados) {
		this.servicosSelecionados = servicosSelecionados;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Time getHorarios() {
		return horarios;
	}

	public void setHorarios(Time horarios) {
		this.horarios = horarios;
	}

	public Set<Usuario> getSetFuncionarios() {
		return setFuncionarios;
	}

	public void setSetFuncionarios(Set<Usuario> setFuncionarios) {
		this.setFuncionarios = setFuncionarios;
	}
	
	

}