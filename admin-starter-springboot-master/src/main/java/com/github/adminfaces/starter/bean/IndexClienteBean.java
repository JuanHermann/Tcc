package com.github.adminfaces.starter.bean;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.primefaces.model.menu.MenuModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.HorarioAgendado;
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
@Getter
@Setter
@Scope("view")
public class IndexClienteBean extends AbastractFormBean<HorarioAgendado, HorarioAgendadoRepository> {
	private static LocalTime HORA_INICIO_EMPRESA = LocalTime.of(7, 0, 0);
	private static LocalTime HORA_INICIO_INTERVALO = LocalTime.of(12, 0, 0);
	private static LocalTime HORA_FINAL_INTERVALO = LocalTime.of(13, 30, 0);
	private static LocalTime HORA_FINAL_EMPRESA = LocalTime.of(20, 0, 0);
	private static LocalTime TEMPO_BUSCA_ENTRE_SERVICOS = LocalTime.of(0, 15, 0);
	private static LocalTime TEMPO_PARA_CANCELAMENTO = LocalTime.of(23, 0, 0);
	private static LocalTime TEMPO_PARA_AGENDAMENTO = LocalTime.of(2, 0, 0);

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
	private boolean editar;
	private boolean mostrarDialog;

	private Date data = Calendar.getInstance().getTime();

	@Autowired
	protected ContextBean context;

	@Autowired
	private UsuarioLogadoBean usuarioLogadoBean;
	private List<Usuario> funcionarios;
	private Set<Usuario> setFuncionarios;
	private Usuario funcionario;
	private Usuario funcionarioDoList;

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
	
	@Autowired
	private UsuarioRepository usuarioRepository;

	private MenuModel menuModel;

	public IndexClienteBean() {
		super(HorarioAgendado.class);
	}

	@Override
	public void init() throws InstantiationException, IllegalAccessException {
		usuarioLogadoBean.setUsuario( usuarioRepository.findById(usuarioLogadoBean.getUsuario().getId()).get());
		
		
		inicioSchedule = HORA_INICIO_EMPRESA.toString();
		finalSchedule = HORA_FINAL_EMPRESA.toString();
		inicioHoraCalendar = HORA_INICIO_EMPRESA.getHour();
		finalHoraCalendar = HORA_FINAL_EMPRESA.getHour();

		timeZoneBrasil = TimeZone.getTimeZone("America/Sao_Paulo");

		servicos = servicoRepository.findByAtivoOrderByNome(true);
		servicos.remove(servicoRepository.findById(1).get());
		servicosSelecionados = new ArrayList<>();
		horarioAgendados = new ArrayList<>();
		editar = false;
		verificarCadastroAceito();

		dataMinima = Calendar.getInstance().getTime();
		funcionario = new Usuario();
		funcionarioDoList = new Usuario();
		funcionarios = new ArrayList<>();
		setFuncionarios = new HashSet<>();
		stringHorario = "Selecione um Horário";
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
		if (mostrarFuncionario()) {
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

			}
			Usuario u = new Usuario();
			u.setNome("Sem Preferência");
			setFuncionarios.add(u);
			setFuncionarios.addAll(funcionariosCorreto);
		}
		buscarHorarios();
	}

	public void buscarHorarios() {
		if (getObjeto().getData().isBefore(LocalDate.now())) {
			getObjeto().setData(LocalDate.now());

		}
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
		if (LocalDate.now().isEqual(getObjeto().getData())) {
			retirarHorariosHoje();
		}
		if (horarios.isEmpty()) {
			stringHorario = "Nenhum horário disponivel nesta data";
		} else {
			stringHorario = "Selecione um horário";
		}

	}

	private void retirarHorariosHoje() {
		LocalDateTime tempoServico, tempoMaximo;
		tempoServico = LocalDateTime.of(getObjeto().getData(), somarLocalTime(TEMPO_PARA_AGENDAMENTO, LocalTime.now()));
		tempoMaximo = LocalDateTime.of(LocalDate.now(), LocalTime.now());
		if (tempoServico.getDayOfMonth() != tempoMaximo.getDayOfMonth()) {

			LocalTime horaAux = LocalTime.now(), h = horarios.get(0);
			horaAux = somarLocalTime(horaAux, TEMPO_PARA_AGENDAMENTO);
			while (h.isBefore(horaAux) && !horarios.isEmpty()) {
				horarios.remove(h);
				if (!horarios.isEmpty()) {
					h = horarios.get(0);
				}
			}
		} else {
			horarios.clear();
		}
	}

	public void salvarAgendamento() {
		if (funcionarioDoList.getId() == null && mostrarFuncionario()) {
			pegarFuncionarioCorrespondenteAoHorario();
		}

		HorarioAgendado agendado = new HorarioAgendado();
		boolean primeiro = true;
		LocalTime tempo = LocalTime.of(0, 0, 0);
		for (Servico servico : servicosSelecionados) {
			if (getObjeto().getId() == null) {
				agendado = new HorarioAgendado();
				agendado.setCliente(usuarioLogadoBean.getUsuario());
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
					agendado.setCliente(usuarioLogadoBean.getUsuario());
					agendado.setData(getObjeto().getData());
				}
				agendado.setHoraInicio(tempo);
				tempo = somarLocalTime(tempo, servico.getTempo());
				agendado.setHoraTermino(tempo);
			}
			if (mostrarFuncionario()) {

				agendado.setUsuarioServico(usuarioServicoRepository
						.findByServicoAndUsuarioAndAtivoOrderByUsuario(servico, funcionarioDoList, true));

			} else {
				agendado.setUsuarioServico(usuarioServicoRepository.findByServicoAndAtivo(servico, true).get(0));
			}
			getRepository().save(agendado);

		}
		stringHorario = "Selecione um horario";
		setObjeto(new HorarioAgendado());
		getObjeto().setData(LocalDate.now());
		if (mostrarFuncionario()) {
			funcionarioDoList = new Usuario();
		}
		servicosSelecionados.clear();
		atualizarLista();
		addDetailMessage("Cadastrado com sucesso");
		context.fecharDialog("inserir");

	}

	private boolean verificaEspacoTempo(LocalTime primeiroHorarioLivre, LocalTime tempoTotalServico,
			LocalTime proximoServico) {
		LocalTime total = somarLocalTime(primeiroHorarioLivre, tempoTotalServico);
		if (total.isBefore(proximoServico) || total.equals(proximoServico)) {
			return true;
		}

		return false;
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

	private void pegarFuncionarioCorrespondenteAoHorario() {
		LocalTime inicio = getObjeto().getHoraInicio(),
				termino = somarLocalTime(getObjeto().getHoraInicio(), tempoTotalServicos);

		boolean possivel = true;
		for (Usuario funcionario : setFuncionarios) {
			if (funcionario.getId() != null) {
				possivel = true;
				for (HorarioAgendado ha : horarioAgendadoRepository.findByFuncionarioAndData(funcionario.getId(),
						getObjeto().getData())) {
					if (ha.getHoraInicio().isBefore(termino) && ha.getHoraTermino().isAfter(inicio)) {
						possivel = false;
					}
				}
				if (possivel == true) {
					// possivel fazer outra combinação aqui
					funcionarioDoList = funcionario;
				}
			}

		}
	}

	private LocalTime subtrairLocalTime(LocalTime tempo, LocalTime tempo2) {
		tempo = tempo.plusHours(-tempo2.getHour());
		tempo = tempo.plusMinutes(-tempo2.getMinute());
		return tempo;
	}

	private LocalTime somarLocalTime(LocalTime tempo, LocalTime tempo2) {
		tempo = tempo.plusHours(tempo2.getHour());
		tempo = tempo.plusMinutes(tempo2.getMinute());
		return tempo;
	}

	public void remover(HorarioAgendado horarioAgendado) throws InstantiationException, IllegalAccessException {
		setObjeto(horarioAgendado);
		if (verificaTempoCancelamento(getObjeto())) {
			super.remover();
			atualizarLista();
			setObjeto(new HorarioAgendado());
		}

	}

	public boolean verificaTempoCancelamento(HorarioAgendado horario) {
		LocalDateTime tempoServico, tempoMaximo;
		tempoServico = LocalDateTime.of(horario.getData(), horario.getHoraInicio());
		tempoMaximo = LocalDateTime.of(LocalDate.now(), LocalTime.now());
		tempoMaximo = tempoMaximo.plusHours(TEMPO_PARA_CANCELAMENTO.getHour());
		tempoMaximo = tempoMaximo.plusMinutes(TEMPO_PARA_CANCELAMENTO.getMinute());
		if (tempoServico.isAfter(tempoMaximo))
			return true;

		return false;
	}

	public void verificarCadastroAceito() {
		if (usuarioLogadoBean.hasRole("ROLE_CLIENTE") || horarioAgendadoRepository
				.findByClienteAndDataGreaterThanEqualOrderByDataAsc(usuarioLogadoBean.getUsuario(), LocalDate.now())
				.size() == 0 || editar == true) {
			if (editar == false) {
				setObjeto(new HorarioAgendado());
				getObjeto().setData(LocalDate.now());
				if (mostrarFuncionario()) {
					funcionarioDoList = new Usuario();
				}
				servicosSelecionados.clear();
			}
			editar = false;
			mostrarDialog = true;

		} else {

			mostrarDialog = false;
		}
	}

	public void carregarObjeto(HorarioAgendado horarioAgendado) {
		servicosSelecionados.clear();
		setObjeto(horarioAgendado);
		servicosSelecionados.add(getObjeto().getUsuarioServico().getServico());
		buscarFuncionarios();
		horarios.add(getObjeto().getHoraInicio());
		funcionarioDoList = getObjeto().getUsuarioServico().getUsuario();
		editar = true;
		verificarCadastroAceito();

	}

	public void mostrar(HorarioAgendado horarioAgendado) {
		System.out.println(horarioAgendado.getHoraInicio());
	}
}