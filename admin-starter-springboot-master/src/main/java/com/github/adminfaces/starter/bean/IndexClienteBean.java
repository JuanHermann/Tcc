package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.HorarioAgendado;
import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.repository.HorarioAgendadoRepository;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.ServicoRepository;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.menu.MenuModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Scope("view")
public class IndexClienteBean extends AbastractFormBean<HorarioAgendado, HorarioAgendadoRepository> {
	private static LocalTime HORA_INICIO_EMPRESA = LocalTime.of(7, 0, 0);
	private static LocalTime HORA_INICIO_INTERVALO = LocalTime.of(12, 0, 0);
	private static LocalTime HORA_FINAL_INTERVALO = LocalTime.of(13, 30, 0);
	private static LocalTime HORA_FINAL_EMPRESA = LocalTime.of(20, 0, 0);
	private static LocalTime TEMPO_BUSCA_ENTRE_SERVICOS = LocalTime.of(0, 15, 0);
	private ScheduleModel eventModel = new DefaultScheduleModel();

	private ScheduleEvent event = new DefaultScheduleEvent();

	private List<HorarioAgendado> horariosCliente;
	private Date dataMinima;
	private String stringHorario;
	private TimeZone timeZoneBrasil;
	private String inicioSchedule;
	private String finalSchedule;
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
	private UsuarioLogadoBean usuarioLogadoBean;
	private List<Usuario> funcionarios;
	private Set<Usuario> setFuncionarios;
	private Usuario funcionario;

	@Autowired
	private HorarioAgendadoRepository horarioAgendadoRepository;
	private List<HorarioAgendado> horarioAgendados;

	@Autowired
	private PermissaoRepository permissaoRepository;

	@Autowired
	private UsuarioServicoRepository usuarioServicoRepository;
	private List<UsuarioServico> usuarioServicos;

	@Autowired
	private ServicoRepository servicoRepository;
	private List<Servico> servicos;
	private List<Servico> servicosSelecionados;

	private MenuModel menuModel;

	public IndexClienteBean() {
		super(HorarioAgendado.class);
	}

	@Override
	public void init() throws InstantiationException, IllegalAccessException {

		inicioSchedule = HORA_INICIO_EMPRESA.toString();
		finalSchedule = HORA_FINAL_EMPRESA.toString();
		inicioHoraCalendar = HORA_INICIO_EMPRESA.getHour();
		finalHoraCalendar = HORA_FINAL_EMPRESA.getHour();

		timeZoneBrasil = TimeZone.getTimeZone("America/Sao_Paulo");

		servicos = servicoRepository.findByAtivoOrderByNome(true);
		servicosSelecionados = new ArrayList<>();

		dataMinima = Calendar.getInstance().getTime();
		funcionario = new Usuario();
		funcionarios = new ArrayList<>();
		setFuncionarios = new HashSet<>();
		stringHorario = "Selecione um Horairio";
		super.init();
		getObjeto().setCliente(usuarioLogadoBean.getUsuario());
		getObjeto().setData(LocalDate.now());
		atualizarLista();
	}

	public void atualizarLista() {
		horariosCliente = horarioAgendadoRepository
				.findByClienteAndDataGreaterThanEqualOrderByDataAsc(usuarioLogadoBean.getUsuario(), LocalDate.now());

	}

	public boolean mostrarFuncionario() {
		return permissaoRepository.findByNome("ROLE_FUNCIONARIO").getUsuarios().size() > 1;

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
		if (servicosSelecionados.size() > 0) {
			tempoTotalServicos = LocalTime.of(0, 0, 0);
			for (Servico servico : servicosSelecionados) {
				tempoTotalServicos = somarLocalTime(tempoTotalServicos, servico.getTempo());
			}
			System.out.println(tempoTotalServicos);
			horariosLivres(tempoTotalServicos);
		} else {
			horarios.clear();
		}

	}

	public void salvarAgendamento() {
		if (mostrarFuncionario()) {
			if (funcionario == null) {
				funcionario = selecionarFuncionarioSemPreferencia();
			}
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
				agendado.setUsuarioServico(usuarioServicoRepository
						.findByServicoAndUsuarioAndAtivoOrderByUsuario(servico, funcionario, true));
				getRepository().save(agendado);

			}
			setObjeto(new HorarioAgendado());
			servicosSelecionados.clear();
			atualizarLista();
		} else {
			if (getObjeto().getId() != null) {
				HorarioAgendado agendado;
				boolean primeiro = true;
				LocalTime tempo = LocalTime.of(0, 0, 0);
				for (Servico servico : servicosSelecionados) {

					if (primeiro) {
						agendado = getObjeto();
						agendado.setHoraInicio(getObjeto().getHoraInicio());

						tempo = somarLocalTime(getObjeto().getHoraInicio(), servico.getTempo());
						agendado.setHoraTermino(tempo);
						primeiro = false;
					} else {
						agendado = new HorarioAgendado();
						agendado.setCliente(getObjeto().getCliente());
						agendado.setData(getObjeto().getData());
						agendado.setHoraInicio(tempo);
						tempo = somarLocalTime(tempo, servico.getTempo());
						agendado.setHoraTermino(tempo);
					}
					agendado.setUsuarioServico(usuarioServicoRepository.findByServicoAndAtivo(servico, true).get(0));
					getRepository().save(agendado);

				}
				setObjeto(new HorarioAgendado());
				servicosSelecionados.clear();
				atualizarLista();

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
					agendado.setUsuarioServico(usuarioServicoRepository.findByServicoAndAtivo(servico, true).get(0));
					getRepository().save(agendado);

				}
				limparObjeto();
				servicosSelecionados.clear();
				atualizarLista();
			}
		}
		context.fecharDialog("inserir");
	}

	private void limparObjeto() {
		setObjeto(new HorarioAgendado());
		getObjeto().setCliente(usuarioLogadoBean.getUsuario());
		getObjeto().setData(LocalDate.now());
		
	}

	private Usuario selecionarFuncionarioSemPreferencia() {
		List<Usuario> lista = new ArrayList<>(setFuncionarios);
		return lista.get(0);
	}

	private boolean verificaEspacoTempo(LocalTime primeiroHorarioLivre, LocalTime tempoTotalServico,
			LocalTime proximoServico) {
		LocalTime total = somarLocalTime(primeiroHorarioLivre, tempoTotalServico);
		if (total.isBefore(proximoServico) || total.equals(proximoServico)) {
			return true;
		}

		return false;
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
		if (horarios.isEmpty()) {
			stringHorario = "Nenhum Horario disponivel nessa Data";
		} else {
			stringHorario = "Selecione um horario";
		}

	}

	private LocalTime somarLocalTime(LocalTime tempo, LocalTime tempo2) {
		tempo = tempo.plusHours(tempo2.getHour());
		tempo = tempo.plusMinutes(tempo2.getMinute());
		return tempo;
	}

	public void remover(Integer id) throws InstantiationException, IllegalAccessException {
		setObjeto(horarioAgendadoRepository.findById(id).get());
		if (true) {
			super.remover();
		}
	}

	public void carregarObjeto(Integer id) {
		servicosSelecionados = new ArrayList<>();
		setObjeto(horarioAgendadoRepository.findById(id).get());
		servicosSelecionados.add(getObjeto().getUsuarioServico().getServico());
		buscarHorarios();
		horarios.add(getObjeto().getHoraInicio());

	}
}