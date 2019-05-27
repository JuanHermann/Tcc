package com.github.adminfaces.starter.bean;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.Empresa;
import com.github.adminfaces.starter.model.HorarioAgendado;
import com.github.adminfaces.starter.model.Permissao;
import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.repository.EmpresaRepository;
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
	private static LocalTime HORA_INICIO_EMPRESA = LocalTime.of(7, 0, 0);
	private static LocalTime HORA_INICIO_INTERVALO = LocalTime.of(12, 0, 0);
	private static LocalTime HORA_FINAL_INTERVALO = LocalTime.of(13, 30, 0);
	private static LocalTime HORA_FINAL_EMPRESA = LocalTime.of(20, 0, 0);
	private static LocalTime TEMPO_BUSCA_ENTRE_SERVICOS = LocalTime.of(0, 15, 0);

	private ScheduleModel eventModel = new DefaultScheduleModel();
	private ScheduleEvent event = new DefaultScheduleEvent();

	private String stringHorario;
	private TimeZone timeZoneBrasil;
	private String inicioSchedule;
	private String finalSchedule;
	private int tempoMinutosSchedule;
	private int inicioHoraCalendar;
	private int finalHoraCalendar;
	private LocalDateTime dataInicioBloqueio;
	private LocalDateTime dataFinalBloqueio;
	private String tipo = "servico";
	private LocalTime tempoTotalServicos;
	private List<LocalTime> horarios;
	private Date data = Calendar.getInstance().getTime();
	
	@Autowired
	protected ContextBean context;

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
	private UsuarioLogadoBean usuarioLogadoBean;

	@Autowired
	private PermissaoRepository permissaoRepository;

	@Autowired
	private HorarioAgendadoRepository horarioAgendadoRepository;
	private List<HorarioAgendado> horarioAgendados;

	@Autowired
	private EmpresaRepository empresaRepository;

	public IndexBean() {
		super(HorarioAgendado.class);

	}

	@PostConstruct
	public void init() throws InstantiationException, IllegalAccessException {
		if (!verificaPermissao()) {
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("indexcliente.jsf");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		super.init();

		if (empresaRepository.findAll().size() > 0) {
			Empresa empresa = empresaRepository.findAll().get(0);
			HORA_INICIO_EMPRESA = empresa.getHoraAbertura();
			HORA_INICIO_INTERVALO = empresa.getInicioIntervalo();
			HORA_FINAL_INTERVALO = empresa.getFinalIntervalo();
			HORA_FINAL_EMPRESA = empresa.getHoraFechamento();
			TEMPO_BUSCA_ENTRE_SERVICOS = empresa.getTempoIntervalo();
		}

		inicioSchedule = HORA_INICIO_EMPRESA.toString();
		finalSchedule = HORA_FINAL_EMPRESA.toString();
		tempoMinutosSchedule = TEMPO_BUSCA_ENTRE_SERVICOS.getMinute();

		inicioHoraCalendar = HORA_INICIO_EMPRESA.getHour();
		finalHoraCalendar = HORA_FINAL_EMPRESA.getHour();

		timeZoneBrasil = TimeZone.getTimeZone("America/Sao_Paulo");
		clientes = new ArrayList<>();
		horarioAgendados = new ArrayList<>();
		buscarClientes();
		servicos = servicoRepository.findByAtivo(true);
		servicosSelecionados = new ArrayList<>();
		cliente = new Usuario();
		funcionario = new Usuario();
		funcionarios = new ArrayList<>();
		setFuncionarios = new HashSet<>();
		stringHorario = "Selecione um Horário";

		atualizarSchedule();
	}

	private boolean verificaPermissao() {
		return usuarioLogadoBean.getUsuario().hasRole("ROLE_ADMIN", usuarioLogadoBean.getUsuario());

	}

	private void atualizarSchedule() {

		horarioAgendados = horarioAgendadoRepository.findAll();
		eventModel = new DefaultScheduleModel();
		Date dataInicio = new Date(), dataFinal = new Date();
		for (HorarioAgendado horario : horarioAgendados) {
			LocalDateTime localDataInicio = LocalDateTime.of(horario.getData(), horario.getHoraInicio());
			LocalDateTime localDataFinal = LocalDateTime.of(horario.getData(), horario.getHoraTermino());
			dataInicio = Date.from(localDataInicio.toInstant(ZoneOffset.UTC));
			dataFinal = Date.from(localDataFinal.toInstant(ZoneOffset.UTC));

			if (horario.getUsuarioServico() == null) {
				DefaultScheduleEvent df = new DefaultScheduleEvent("Bloqueio", dataInicio, dataFinal, horario);
				df.setStyleClass("btn-danger");
				eventModel.addEvent(df);

			} else {

				eventModel.addEvent(new DefaultScheduleEvent(
						horario.getCliente().getNome() + " - " + horario.getUsuarioServico().getServico().getNome(),
						dataInicio, dataFinal, horario));
			}

		}

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
		if (servicosSelecionados != null && mostrarFuncionario()) {
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

		tempoTotalServicos = LocalTime.of(0, 0, 0);
		for (Servico servico : servicosSelecionados) {
			tempoTotalServicos = somarLocalTime(tempoTotalServicos, servico.getTempo());
		}
		System.out.println(tempoTotalServicos);
		horariosLivres(tempoTotalServicos);

	}

	private void horariosLivres(LocalTime TempoTotalServicos) {
		horarios = new ArrayList<>();
		LocalTime horaAuxiliar = HORA_INICIO_EMPRESA;
		while (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos, HORA_INICIO_INTERVALO) == true) {
			horarios.add(horaAuxiliar);
			horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);
		}
		horaAuxiliar = HORA_FINAL_INTERVALO;
		while (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos, HORA_FINAL_EMPRESA) == true) {
			horarios.add(horaAuxiliar);
			horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);
		}

		horarioAgendados = horarioAgendadoRepository.findByDataOrderByHoraInicio(getObjeto().getData());
		if (!horarioAgendados.isEmpty()) {
			for (HorarioAgendado horarioAgendado : horarioAgendados) {
				if (verificaEspacoTempo(HORA_INICIO_EMPRESA, TempoTotalServicos,
						horarioAgendado.getHoraInicio()) == false
						&& horarioAgendado.getHoraInicio().isBefore(HORA_INICIO_INTERVALO)) {
					horaAuxiliar = HORA_INICIO_EMPRESA;
					while (horaAuxiliar.isBefore(horarioAgendado.getHoraTermino())) {
						horarios.remove(horaAuxiliar);
						horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);
					}
				}
				if (verificaEspacoTempo(HORA_FINAL_INTERVALO, TempoTotalServicos,
						horarioAgendado.getHoraInicio()) == false
						&& horarioAgendado.getHoraInicio().isAfter(HORA_FINAL_INTERVALO)) {
					horaAuxiliar = HORA_FINAL_INTERVALO;
					while (horaAuxiliar.isBefore(horarioAgendado.getHoraTermino())) {
						horarios.remove(horaAuxiliar);
						horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);
					}
				} else {
					horaAuxiliar = subtrairLocalTime(horarioAgendado.getHoraInicio(), TempoTotalServicos);
					horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);
					while (horaAuxiliar.isBefore(horarioAgendado.getHoraTermino())) {
						horarios.remove(horaAuxiliar);
						horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);
					}
				}
			}
		}
	}

	private boolean verificaEspacoTempo(LocalTime primeiroHorarioLivre, LocalTime tempoTotalServico,
			LocalTime proximoServico) {
		LocalTime total = somarLocalTime(primeiroHorarioLivre, tempoTotalServico);
		if (total.isBefore(proximoServico) || total.equals(proximoServico)) {
			return true;
		}

		return false;
	}

	public boolean mostrarForm() {
		return "servico".equalsIgnoreCase(tipo);

	}

	public boolean mostrarFuncionario() {
		return permissaoRepository.findByNome("ROLE_FUNCIONARIO").getUsuarios().size() > 1;

	}

	public void excluirAgendamento() throws InstantiationException, IllegalAccessException {
		super.remover();
		atualizarSchedule();
	}

	public void salvarAgendamento() {
		if (mostrarForm() == true) {
			System.out.println("agendar");
			if (mostrarFuncionario()) {
				for (Servico servico : servicosSelecionados) {

				}
			} else {

				HorarioAgendado agendado= new HorarioAgendado();
				boolean primeiro = true;
				LocalTime tempo = LocalTime.of(0, 0, 0);
				for (Servico servico : servicosSelecionados) {
					if (getObjeto().getId() == null) {
						agendado = new HorarioAgendado();
						agendado.setCliente(getObjeto().getCliente());
						agendado.setData(getObjeto().getData());
					}
					if (primeiro) {
						if (getObjeto().getId() != null) {
							agendado = getObjeto();
						}
						agendado.setHoraInicio(getObjeto().getHoraInicio());

						tempo = somarLocalTime(getObjeto().getHoraInicio(), servico.getTempo());
						agendado.setHoraTermino(tempo);
						primeiro = false;
					} else {
						if (getObjeto().getId() != null) {
							agendado = new HorarioAgendado();
							agendado.setCliente(getObjeto().getCliente());
							agendado.setData(getObjeto().getData());
						}
						agendado.setHoraInicio(tempo);
						tempo = somarLocalTime(tempo, servico.getTempo());
						agendado.setHoraTermino(tempo);
					}
					agendado.setUsuarioServico(usuarioServicoRepository.findByServicoAndAtivo(servico, true).get(0));
					getRepository().save(agendado);

				}
				setObjeto(new HorarioAgendado());
				servicosSelecionados.clear();
				atualizarSchedule();
				addDetailMessage("Cadastrado com sucesso");
				context.fecharDialog("inserir");

			}
		} else {
			System.out.println("bloquear");
			if (dataInicioBloqueio.toLocalDate().equals(dataFinalBloqueio.toLocalDate())) {
				System.out.println("igual");
				HorarioAgendado bloquear = new HorarioAgendado();
				bloquear.setData(dataInicioBloqueio.toLocalDate());
				bloquear.setHoraInicio(dataInicioBloqueio.toLocalTime());
				bloquear.setHoraTermino(dataFinalBloqueio.toLocalTime());
				getRepository().save(bloquear);

			} else {
				System.out.println("diferente");
			}

		}

	}

	private LocalTime somarLocalTime(LocalTime tempo, LocalTime tempo2) {
		tempo = tempo.plusHours(tempo2.getHour());
		tempo = tempo.plusMinutes(tempo2.getMinute());
		return tempo;
	}

	private LocalTime subtrairLocalTime(LocalTime tempo, LocalTime tempo2) {
		tempo = tempo.plusHours(-tempo2.getHour());
		tempo = tempo.plusMinutes(-tempo2.getMinute());
		return tempo;
	}

	public void addEvent() {
		if (event.getId() == null)
			eventModel.addEvent(event);
		else
			eventModel.updateEvent(event);

		event = new DefaultScheduleEvent();
	}

	public void onEventSelect(SelectEvent selectEvent) {
		
		servicosSelecionados = new ArrayList<>();
		
		event = (ScheduleEvent) selectEvent.getObject();
		setObjeto((HorarioAgendado) event.getData());
		if (getObjeto().getUsuarioServico() == null) {
			tipo = "bloqueio";
			dataInicioBloqueio = dataInicioBloqueio.of(getObjeto().getData(), getObjeto().getHoraInicio());
			dataFinalBloqueio = dataFinalBloqueio.of(getObjeto().getData(), getObjeto().getHoraTermino());
		} else {
			tipo = "servico";
			servicosSelecionados.add(getObjeto().getUsuarioServico().getServico());
			buscarHorarios();
			horarios.add(getObjeto().getHoraInicio());
			
		}

	}

	public void onDateSelect(SelectEvent selectEvent) {
		setObjeto(new HorarioAgendado());
		servicosSelecionados = new ArrayList<>();
		Date dataSelecionada = (Date) selectEvent.getObject();
		event = new DefaultScheduleEvent("", dataSelecionada, dataSelecionada);
		getObjeto().setData(dataSelecionada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		dataInicioBloqueio = dataInicioBloqueio.of(getObjeto().getData(), HORA_INICIO_EMPRESA);
		dataFinalBloqueio = dataFinalBloqueio.of(getObjeto().getData(), HORA_FINAL_EMPRESA);
	}

	public void onEventMove(ScheduleEntryMoveEvent event) {
		setObjeto((HorarioAgendado) event.getScheduleEvent().getData());
		getObjeto().setData(getObjeto().getData().plusDays(event.getDayDelta()));
		getObjeto().setHoraInicio(getObjeto().getHoraInicio().plusMinutes(event.getMinuteDelta()));
		getObjeto().setHoraTermino(getObjeto().getHoraTermino().plusMinutes(event.getMinuteDelta()));
		getRepository().save(getObjeto());
		setObjeto(new HorarioAgendado());
	}

	public void onEventResize(ScheduleEntryResizeEvent event) {
		setObjeto((HorarioAgendado) event.getScheduleEvent().getData());
		getObjeto().setHoraTermino(getObjeto().getHoraTermino().plusMinutes(event.getMinuteDelta()));
		getRepository().save(getObjeto());
		setObjeto(new HorarioAgendado());

	}


}