package com.github.adminfaces.starter.bean;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

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
import com.github.adminfaces.starter.model.HorarioLivre;
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
public class HorarioBean extends AbastractFormBean<HorarioAgendado, HorarioAgendadoRepository> {
	private static LocalTime HORA_INICIO_EMPRESA = LocalTime.of(7, 0, 0);
	private static LocalTime HORA_INICIO_INTERVALO = LocalTime.of(12, 0, 0);
	private static LocalTime HORA_FINAL_INTERVALO = LocalTime.of(13, 30, 0);
	private static LocalTime HORA_FINAL_EMPRESA = LocalTime.of(20, 0, 0);
	private static LocalTime TEMPO_BUSCA_ENTRE_SERVICOS = LocalTime.of(0, 15, 0);

	private boolean isFuncionario;

	private String stringHorario;
	private TimeZone timeZoneBrasil;
	private String inicioSchedule;
	private String finalSchedule;
	private int tempoMinutosSchedule;
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
	private List<Usuario> setFuncionarios;
	private List<Usuario> setFuncionariosBloqueio;
	private List<Usuario> funcionariosAgenda;
	private Usuario funcionario;
	private Usuario funcionarioDoList;
	private Usuario funcionarioDoBloqueio;
	private Usuario funcionarioDaAgenda;
	private Usuario cliente;

	private List<HorarioLivre> lista;

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

	public HorarioBean() {
		super(HorarioAgendado.class);

	}

	@PostConstruct
	public void init() throws InstantiationException, IllegalAccessException {
		verificaPermissao();
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

		timeZoneBrasil = TimeZone.getTimeZone("America/Sao_Paulo");
		clientes = new ArrayList<>();
		horarioAgendados = new ArrayList<>();
		servicos = servicoRepository.findByAtivoOrderByNome(true);
		servicos.remove(servicoRepository.findById(1).get());
		servicosSelecionados = new ArrayList<>();
		funcionario = new Usuario();
		funcionarioDoList = new Usuario();
		funcionarioDoBloqueio = new Usuario();
		funcionarioDaAgenda = new Usuario();
		funcionarios = new ArrayList<>();

		lista = new ArrayList<>();
		atualizarSchedule();
	}

	private void verificaPermissao() {
		isFuncionario = true;
		String tipoUsuario = "";
		if (Usuario.hasRole("ROLE_ADMIN", usuarioLogadoBean.getUsuario())) {
			tipoUsuario = "admin";
			isFuncionario = false;
		} else if (Usuario.hasRole("ROLE_ATENDENTE", usuarioLogadoBean.getUsuario())) {
			tipoUsuario = "atendente";
			isFuncionario = false;
		}

		if (tipoUsuario == "") {
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect("indexcliente.jsf");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void atualizardataTable() {

		lista.clear();
		List<String> horarios = new ArrayList<>();
		for(Usuario u : Usuario.filtraPorRole(usuarioRepository.findByAtivoOrderByNome(true),
				"ROLE_FUNCIONARIO")) {
			
			
		}

	}

	public void buscarFuncionarios() {
			List<Usuario> funcionariosCorreto = new ArrayList<>();
			boolean primeiro = true;
			setFuncionarios.clear();
			for (Servico servico : servicosSelecionados) {
				List<Usuario> funcionarios = new ArrayList<>();
				for (UsuarioServico usuarioServico : usuarioServicoRepository
						.findByServicoAndAtivoOrderByUsuario(servico, true)) {
					funcionarios.add(usuarioServico.getUsuario());
				}
				if (primeiro) {
					funcionariosCorreto.addAll(funcionarios);
					primeiro = false;
				}
				for (Usuario u : funcionariosCorreto) {
					if (!funcionarios.contains(u)) {
						funcionariosCorreto.remove(u);
					}
				}

			Usuario u = new Usuario();
			u.setNome("Sem Preferência");
			setFuncionarios.add(u);
			setFuncionarios.addAll(funcionariosCorreto);
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
		if (mostrarFuncionario()) {
			if (funcionarioDoList.getId() == null) {
				System.out.println("nem preferencia");
				List<LocalTime> auxHorarios = new ArrayList<>();
				auxHorarios.addAll(horarios);
				Set<LocalTime> horariosFuncionarios = new HashSet<>();
				for (Usuario funcionario : setFuncionarios) {
					if (funcionario.getId() != null) {
						horarios.clear();
						horarios.addAll(auxHorarios);
						horarioAgendados = horarioAgendadoRepository.findByFuncionarioAndData(funcionario.getId(),
								getObjeto().getData());
						retirarHorariosOcupados(TempoTotalServicos);
						horariosFuncionarios.addAll(horarios);
					}
				}
				horarios.clear();
				horarios.addAll(horariosFuncionarios);
				Collections.sort(horarios);
			} else {
				horarioAgendados = horarioAgendadoRepository.findByFuncionarioAndData(funcionarioDoList.getId(),
						getObjeto().getData());
				retirarHorariosOcupados(TempoTotalServicos);
			}
		} else {
			horarioAgendados = horarioAgendadoRepository.findByDataOrderByHoraInicio(getObjeto().getData());
			retirarHorariosOcupados(TempoTotalServicos);
		}
		if (horarios.isEmpty()) {
			stringHorario = "Nenhum Horario disponivel nesta Data";
		} else {
			stringHorario = "Selecione um horario";
		}

	}

	private void retirarHorariosOcupados(LocalTime TempoTotalServicos) {

		LocalTime horaAuxiliar = HORA_INICIO_EMPRESA;
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

}