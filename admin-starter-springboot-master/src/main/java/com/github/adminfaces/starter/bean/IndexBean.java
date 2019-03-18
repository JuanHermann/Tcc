package com.github.adminfaces.starter.bean;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

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
import com.github.adminfaces.starter.model.Permissao;
import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.repository.HorarioAgendadoRepository;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.ServicoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;

import lombok.Getter;
import lombok.Setter;

@Component
@Scope("view")
@Getter
@Setter
public class IndexBean extends AbastractFormBean<HorarioAgendado, HorarioAgendadoRepository> {
	private static Time HORA_ENTRE_CADASTRO = new Time(2, 0, 0);
	private static Time HORA_INICIO_EMPRESA = new Time(7, 0, 0);
	private static Time HORA_INICIO_INTERVALO = new Time(12, 0, 0);
	private static Time HORA_FINAL_INTERVALO = new Time(13, 30, 0);
	private static Time HORA_FINAL_EMPRESA = new Time(20, 0, 0);
	private static Time TEMPO_BUSCA_ENTRE_SERVICOS = new Time(0, 15, 0);
	private ScheduleModel eventModel = new DefaultScheduleModel();

	private ScheduleEvent event = new DefaultScheduleEvent();

	private ScheduleEvent dataFinal;
	private String tipo = "servico";
	private Time tempoTotalServicos;
	private List<Time> horarios;
	private Date data = Calendar.getInstance().getTime();

	@Autowired
	private UsuarioRepository usuarioRepository;
	private List<Usuario> clientes;
	private List<Usuario> funcionarios;
	private Set<Usuario> setFuncionarios;
	private Usuario funcionario;
	private Usuario cliente;

	@Autowired
	private ServicoRepository servicoRepository;
	private List<Servico> servicos;
	private List<Servico> servicosSelecionados;

	@Autowired
	private UsuarioServicoRepository usuarioServicoRepository;
	private List<UsuarioServico> usuarioServicos;

	@Autowired
	private PermissaoRepository permissaoRepository;

	@Autowired
	private HorarioAgendadoRepository horarioAgendadoRepository;
	private List<HorarioAgendado> horarioAgendados;

	public IndexBean() {
		super(HorarioAgendado.class);

	}

	@PostConstruct
	public void init() throws InstantiationException, IllegalAccessException {
		super.init();
		clientes = new ArrayList<>();
		buscarClientes();
		servicos = servicoRepository.findAll();
		cliente = new Usuario();
		funcionario = new Usuario();
		funcionarios = new ArrayList<>();
		setFuncionarios = new HashSet<>();

		horarioAgendados = horarioAgendadoRepository.findAll();
		
		eventModel = new DefaultScheduleModel();
		for(HorarioAgendado horario: horarioAgendados) {
			eventModel.addEvent(new DefaultScheduleEvent(horario.getUsuarioServico().getServico().getNome() +"\n Cliente " +horario.getCliente().getNome(), horario.getHoraInicio(), horario.getHoraTermino()));
		}
		eventModel.addEvent(new DefaultScheduleEvent("Champions League Match", previousDay8Pm(), previousDay11Pm()));
		eventModel.addEvent(new DefaultScheduleEvent("Birthday Party", today1Pm(), today6Pm()));
		eventModel.addEvent(new DefaultScheduleEvent("Birthday top", today6Pm(), today7Pm()));
		eventModel.addEvent(new DefaultScheduleEvent("Breakfast at Tiffanys", nextDay9Am(), nextDay11Am()));
		eventModel.addEvent(new DefaultScheduleEvent("Plant the new garden stuff", theDayAfter3Pm(), fourDaysLater3pm()));

	}

	private void buscarClientes() {
		Permissao permissao = permissaoRepository.findByNome("ROLE_CLIENTE");
		clientes = filtrarCliente(permissao.getUsuarios());
	}

	private List<Usuario> filtrarCliente(List<Usuario> lista) {
		List<Usuario> usuarios = lista;
		List<Usuario> pesquisa = new ArrayList<>();
		for (Usuario usuario : usuarios) {
			List<Permissao> permissoes = usuario.getPermissoes();
			boolean role = false;
			for (Permissao p : permissoes) {
				if (p.getNome().equals("ROLE_ATENDENTE")) {
					role = false;
					break;
				} else if (p.getNome().equals("ROLE_FUNCIONARIO")) {
					role = false;
					break;
				} else if (p.getNome().equals("ROLE_CLIENTE")) {
					role = true;
					break;
				}
			}
			if (role == true) {
				pesquisa.add(usuario);
			}
		}

		return pesquisa;
	}

	public void buscarFuncionarios() {
		System.out.println("passou");
		if (servicosSelecionados != null) {
			for (Servico servico : servicosSelecionados) {
				usuarioServicos = usuarioServicoRepository.findByServicoOrderByUsuario(servico);
				for (UsuarioServico usuarioServico : usuarioServicos) {
					setFuncionarios.add(usuarioServico.getUsuario());
					System.out.println("top");
				}
			}
		}
		buscarHorarios();
	}

	public void buscarHorarios() {
		GregorianCalendar gc = new GregorianCalendar();
		int a = 0;
		tempoTotalServicos = new Time(0, 0, 0);
		for (Servico servico : servicosSelecionados) {
			tempoTotalServicos = somarTime(tempoTotalServicos, servico.getTempo());
		}
		System.out.println(tempoTotalServicos);
		horariosLivres(tempoTotalServicos);

	}

	private void horariosLivres(Time TempoTotalServicos) {
		horarios = new ArrayList<>();
		horarioAgendados = horarioAgendadoRepository.findByDataOrderByHoraInicio(getObjeto().getData());
		if (!horarioAgendados.isEmpty()) {
			Time horaAuxiliar = HORA_INICIO_EMPRESA;
			for (HorarioAgendado horarioAgendado : horarioAgendados) {

				if (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos, horarioAgendado.getHoraInicio())) {
					horarios.add(horaAuxiliar);
					horaAuxiliar = somarTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);

					while (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos,
							horarioAgendado.getHoraInicio()) == true
							&& verificaEspacoTempo(horaAuxiliar, TempoTotalServicos, HORA_INICIO_INTERVALO) == true) {
						horarios.add(horaAuxiliar);
						horaAuxiliar = somarTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);

					}
					if (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos,
							horarioAgendado.getHoraInicio()) == true) {
						horaAuxiliar = HORA_FINAL_INTERVALO;

						while (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos,
								horarioAgendado.getHoraInicio()) == true
								&& verificaEspacoTempo(horaAuxiliar, TempoTotalServicos, HORA_FINAL_EMPRESA) == true) {
							horarios.add(horaAuxiliar);
							horaAuxiliar = somarTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);

						}
					}

				}
				horaAuxiliar = horarioAgendado.getHoraTermino();

			}
			if (horaAuxiliar.before(HORA_FINAL_EMPRESA)) {
				if (horaAuxiliar.before(HORA_INICIO_INTERVALO)) {
					while (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos, HORA_INICIO_INTERVALO) == true) {
						horarios.add(horaAuxiliar);
						horaAuxiliar = somarTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);

					}

					horaAuxiliar = HORA_FINAL_INTERVALO;

					while (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos, HORA_FINAL_EMPRESA) == true) {
						horarios.add(horaAuxiliar);
						horaAuxiliar = somarTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);

					}
				}else {
					if(horaAuxiliar.before(HORA_FINAL_INTERVALO)) {
						horaAuxiliar = HORA_FINAL_INTERVALO;
					}
					while (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos, HORA_FINAL_EMPRESA) == true) {
						horarios.add(horaAuxiliar);
						horaAuxiliar = somarTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);

					}
				}
			}
		} else {
			Time horario = HORA_INICIO_EMPRESA;

			while (verificaEspacoTempo(horario, TempoTotalServicos, HORA_INICIO_INTERVALO) == true) {
				horarios.add(horario);
				horario = somarTime(horario, TEMPO_BUSCA_ENTRE_SERVICOS);

			}

			horario = HORA_FINAL_INTERVALO;

			while (verificaEspacoTempo(horario, TempoTotalServicos, HORA_FINAL_EMPRESA) == true) {
				horarios.add(horario);
				horario = somarTime(horario, TEMPO_BUSCA_ENTRE_SERVICOS);

			}
		}

	}

	private boolean verificaEspacoTempo(Time primeiroHorarioLivre, Time tempoTotalServico, Time proximoServico) {
		Time total = somarTime(primeiroHorarioLivre, tempoTotalServico);
		if (timeToInt(total) <= timeToInt(proximoServico)) {
			return true;
		}

		return false;
	}

	private int timeToInt(Time tempoConverter) {

		String[] tempo = String.valueOf(tempoConverter).split(":");
		int hora = Integer.parseInt(tempo[0]), minuto = Integer.parseInt(tempo[1]);
		return (hora * 100) + minuto;

	}

	public Time somarTime(Time tempo1, Time tempo2) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(tempo1.getTime());
		String[] tempo = String.valueOf(tempo2).split(":");
		int hora = Integer.parseInt(tempo[0]), minuto = Integer.parseInt(tempo[1]);
		if (hora > 0) {
			gc.add(Calendar.HOUR, hora);
		}
		if (minuto > 0) {
			gc.add(Calendar.MINUTE, minuto);
		}

		Time soma = new Time(gc.getTime().getTime());
		return soma;
	}

	public boolean mostrarForm() {
		return "servico".equalsIgnoreCase(tipo);

	}

	public boolean mostrarFuncionario() {
		return permissaoRepository.findByNome("ROLE_FUNCIONARIO").getUsuarios().size() > 1;

	}

	public void salvarAgendamento() {
		if (mostrarForm() == true) {
			System.out.println("agendar");
			if (mostrarFuncionario()) {
				for (Servico servico : servicosSelecionados) {
					getObjeto().setUsuarioServico(
							usuarioServicoRepository.findByServicoAndUsuarioOrderByUsuario(servico, funcionario));
				}
			} else {
				HorarioAgendado agendado;
				boolean primeiro = true;
				Time tempo = new Time(0, 0, 0);
				for (Servico servico : servicosSelecionados) {
					agendado = new HorarioAgendado();
					agendado.setCliente(getObjeto().getCliente());
					agendado.setData(getObjeto().getData());
					if (primeiro) {
						agendado.setHoraInicio(getObjeto().getHoraInicio());
						tempo = somarTime(getObjeto().getHoraInicio(), servico.getTempo());
						agendado.setHoraTermino(tempo);
						primeiro = false;
					} else {
						agendado.setHoraInicio(tempo);
						tempo = somarTime(tempo, servico.getTempo());
						agendado.setHoraTermino(tempo);
					}
					agendado.setUsuarioServico(usuarioServicoRepository.findByServico(servico));
					getRepository().save(agendado);
					
				}
				setObjeto(new HorarioAgendado());

			}
		} else {
			System.out.println("bloquear");
			HorarioAgendado bloquear = new HorarioAgendado();
			bloquear.setData(event.getStartDate());

		}

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

	private Date today7Pm() {
		Calendar t = (Calendar) today().clone();
		t.set(Calendar.AM_PM, Calendar.PM);
		t.set(Calendar.HOUR, 7);

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

}