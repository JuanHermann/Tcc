package com.github.adminfaces.starter.bean;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

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
	private static LocalTime HORA_ENTRE_CADASTRO = LocalTime.of(2, 0, 0);
	private static LocalTime HORA_INICIO_EMPRESA = LocalTime.of(7, 0, 0);
	private static LocalTime HORA_INICIO_INTERVALO = LocalTime.of(12, 0, 0);
	private static LocalTime HORA_FINAL_INTERVALO = LocalTime.of(13, 30, 0);
	private static LocalTime HORA_FINAL_EMPRESA = LocalTime.of(20, 0, 0);
	private static LocalTime TEMPO_BUSCA_ENTRE_SERVICOS = LocalTime.of(0, 15, 0);
	private ScheduleModel eventModel = new DefaultScheduleModel();

	private ScheduleEvent event = new DefaultScheduleEvent();

	private TimeZone timeZoneBrasil;
	private String inicioSchedule;
	private String finalSchedule;
	private ScheduleEvent dataFinal;
	private String tipo = "servico";
	private LocalTime tempoTotalServicos;
	private List<LocalTime> horarios;
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
		inicioSchedule = HORA_INICIO_EMPRESA.toString();
		finalSchedule = HORA_FINAL_EMPRESA.toString();

		timeZoneBrasil = TimeZone.getTimeZone("America/Sao_Paulo");
		clientes = new ArrayList<>();
		buscarClientes();
		servicos = servicoRepository.findAll();
		cliente = new Usuario();
		funcionario = new Usuario();
		funcionarios = new ArrayList<>();
		setFuncionarios = new HashSet<>();

		atualizarSchedule();

	}

	private void atualizarSchedule() {

		horarioAgendados = horarioAgendadoRepository.findAll();
		eventModel = new DefaultScheduleModel();
		Date dataInicio = new Date(),dataFinal = new Date();
		for (HorarioAgendado horario : horarioAgendados) {
			LocalDateTime localDataInicio = LocalDateTime.of(horario.getData(), horario.getHoraInicio()); 
			LocalDateTime localDataFinal = LocalDateTime.of(horario.getData(),horario.getHoraTermino());
			dataInicio = Date.from(localDataInicio.toInstant(ZoneOffset.UTC));
			dataFinal = Date.from(localDataFinal.toInstant(ZoneOffset.UTC));
			eventModel.addEvent(new DefaultScheduleEvent("Birthday Party", today1Pm(), today6Pm()));
			if (horario.getUsuarioServico().getServico() == null) {
				eventModel.addEvent(
						new DefaultScheduleEvent("Bloqueio", dataInicio, dataFinal,
						"btn-danger"));
			} else {
				
				eventModel
						.addEvent(new DefaultScheduleEvent(
								horario.getUsuarioServico().getServico().getNome() + " Cliente "
										+ horario.getCliente().getNome(),
								dataInicio, dataFinal));
			}
	System.out.println();

		}
		eventModel.addEvent(
				new DefaultScheduleEvent("Bloqueio", previousDay8Pm(), previousDay11Pm(), "btn-danger"));
		eventModel.addEvent(new DefaultScheduleEvent("Birthday top", today6Pm(), today7Pm()));
		eventModel.addEvent(new DefaultScheduleEvent("Breakfast at Tiffanys", nextDay9Am(), nextDay11Am()));


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
				}
			}
		}
		buscarHorarios();
	}

	public void buscarHorarios() {
		
		tempoTotalServicos = LocalTime.of(0, 0,0);
		for (Servico servico : servicosSelecionados) {
			tempoTotalServicos = somarLocalTime(tempoTotalServicos, servico.getTempo());
		}
		System.out.println(tempoTotalServicos);
		horariosLivres(tempoTotalServicos);

	}

	private void horariosLivres(LocalTime TempoTotalServicos) {
		horarios = new ArrayList<>();
		horarioAgendados = horarioAgendadoRepository.findByDataOrderByHoraInicio(getObjeto().getData());
		if (!horarioAgendados.isEmpty()) {
			
			LocalTime horaAuxiliar = LocalTime.of(0, 0);
			horaAuxiliar = HORA_INICIO_EMPRESA;
			for (HorarioAgendado horarioAgendado : horarioAgendados) {

				if (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos, horarioAgendado.getHoraInicio())) {
					horarios.add(horaAuxiliar);
					horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);

					while (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos,
							horarioAgendado.getHoraInicio()) == true
							&& verificaEspacoTempo(horaAuxiliar, TempoTotalServicos, HORA_INICIO_INTERVALO) == true) {
						horarios.add(horaAuxiliar);
						horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);

					}
					if (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos,
							horarioAgendado.getHoraInicio()) == true) {
						horaAuxiliar = HORA_FINAL_INTERVALO;

						while (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos,
								horarioAgendado.getHoraInicio()) == true
								&& verificaEspacoTempo(horaAuxiliar, TempoTotalServicos, HORA_FINAL_EMPRESA) == true) {
							horarios.add(horaAuxiliar);
							horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);

						}
					}

				}
				horaAuxiliar = horarioAgendado.getHoraTermino();

			}
			if (horaAuxiliar.isBefore(HORA_FINAL_EMPRESA)) {
				if (horaAuxiliar.isBefore(HORA_INICIO_INTERVALO)) {
					while (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos, HORA_INICIO_INTERVALO) == true) {
						horarios.add(horaAuxiliar);
						horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);

					}

					horaAuxiliar = HORA_FINAL_INTERVALO;

					while (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos, HORA_FINAL_EMPRESA) == true) {
						horarios.add(horaAuxiliar);
						horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);

					}
				} else {
					if (horaAuxiliar.isBefore(HORA_FINAL_INTERVALO)) {
						horaAuxiliar = HORA_FINAL_INTERVALO;
					}
					while (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos, HORA_FINAL_EMPRESA) == true) {
						horarios.add(horaAuxiliar);
						horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);

					}
				}
			}
		} else {
			LocalTime horario = HORA_INICIO_EMPRESA;

			while (verificaEspacoTempo(horario, TempoTotalServicos, HORA_INICIO_INTERVALO) == true) {
				horarios.add(horario);
				horario = somarLocalTime(horario, TEMPO_BUSCA_ENTRE_SERVICOS);

			}

			horario = HORA_FINAL_INTERVALO;

			while (verificaEspacoTempo(horario, TempoTotalServicos, HORA_FINAL_EMPRESA) == true) {
				horarios.add(horario);
				horario = somarLocalTime(horario, TEMPO_BUSCA_ENTRE_SERVICOS);

			}
		}

	}

	private boolean verificaEspacoTempo(LocalTime primeiroHorarioLivre, LocalTime tempoTotalServico, LocalTime proximoServico) {
		LocalTime total = somarLocalTime(primeiroHorarioLivre, tempoTotalServico);
		if (total.isBefore(proximoServico)  || total.equals(proximoServico)){
			return true;
		}

		return false;
	}

	private int timeToInt(LocalTime tempoConverter) {

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
				LocalTime tempo = LocalTime.of(0, 0, 0);
				for (Servico servico : servicosSelecionados) {
					agendado = new HorarioAgendado();
					agendado.setCliente(getObjeto().getCliente());
					agendado.setData(getObjeto().getData());
					if (primeiro) {
						agendado.setHoraInicio(getObjeto().getHoraInicio());
						
						tempo = somarLocalTime(getObjeto().getHoraInicio(), servico.getTempo());
						agendado.setHoraTermino(tempo);
						primeiro = false;
					} else {
						agendado.setHoraInicio(tempo);
						tempo = somarLocalTime(tempo, servico.getTempo());
						agendado.setHoraTermino(tempo);
					}
					agendado.setUsuarioServico(usuarioServicoRepository.findByServico(servico));
					getRepository().save(agendado);
					

				}
				setObjeto(new HorarioAgendado());
				atualizarSchedule();
			}
		} else {
			System.out.println("bloquear");
			if(getObjeto().getData().equals(dataFinal)) {
				System.out.println("igual");
			}else {
				System.out.println("diferente");
			}
			HorarioAgendado bloquear = new HorarioAgendado();

		}

	}

	private LocalTime somarLocalTime(LocalTime tempo, LocalTime tempo2) {
		tempo = tempo.plusHours(tempo2.getHour());
		tempo = tempo.plusMinutes(tempo2.getMinute());
		return tempo;
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
		Date dataSelecionada = (Date) selectEvent.getObject();
		event = new DefaultScheduleEvent("", dataSelecionada, dataSelecionada);
		getObjeto().setData( dataSelecionada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
	}

	public void onEventMove(ScheduleEntryMoveEvent event) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved",
				"Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

		addDetailMessage("Serviço Transferido /n" + "Day delta:" + event.getDayDelta() + ", Minute delta:"
				+ event.getMinuteDelta());
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