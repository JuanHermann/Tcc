package com.github.adminfaces.starter.bean;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.Empresa;
import com.github.adminfaces.starter.model.HorarioAgendado;
import com.github.adminfaces.starter.model.HorarioLivre;
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
	private LocalTime tempoTotalServicos;
	private List<LocalTime> horarios;
	private List<LocalTime> todosHorarios;

	@Autowired
	protected ContextBean context;

	@Autowired
	private UsuarioRepository usuarioRepository;
	private List<Usuario> setFuncionarios;
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
		getObjeto().setData(LocalDate.now());

		if (empresaRepository.findAll().size() > 0) {
			Empresa empresa = empresaRepository.findAll().get(0);
			HORA_INICIO_EMPRESA = empresa.getHoraAbertura();
			HORA_INICIO_INTERVALO = empresa.getInicioIntervalo();
			HORA_FINAL_INTERVALO = empresa.getFinalIntervalo();
			HORA_FINAL_EMPRESA = empresa.getHoraFechamento();
			TEMPO_BUSCA_ENTRE_SERVICOS = empresa.getTempoIntervalo();
		}

		horarioAgendados = new ArrayList<>();
		servicos = servicoRepository.findByAtivoOrderByNome(true);
		servicos.remove(servicoRepository.findById(1).get());
		servicosSelecionados = new ArrayList<>();
		setFuncionarios = new ArrayList<>();
		horarios = new ArrayList<>();
		lista = new ArrayList<>();
		todosHorarios = buscarTodosHorarios(LocalTime.of(0, 0));
		atualizardataTable();
	}

	private List<LocalTime> buscarTodosHorarios(LocalTime TempoTotalServicos ) {
		List<LocalTime> horarios = new ArrayList<>();
		LocalTime horaAuxiliar = HORA_INICIO_EMPRESA;
		while (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos, HORA_INICIO_INTERVALO) == true) {
			horarios.add(horaAuxiliar);
			horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);
		}
		if(horaAuxiliar.isBefore(HORA_INICIO_INTERVALO)) {
			while(horaAuxiliar.isBefore(HORA_INICIO_INTERVALO)) {
				horarios.add(null);
				horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);
			}
		}
		horaAuxiliar = HORA_FINAL_INTERVALO;
		while (verificaEspacoTempo(horaAuxiliar, TempoTotalServicos, HORA_FINAL_EMPRESA) == true) {
			horarios.add(horaAuxiliar);
			horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);
		}
		if(horaAuxiliar.isBefore(HORA_FINAL_EMPRESA)) {
			while(horaAuxiliar.isBefore(HORA_FINAL_EMPRESA)) {
				horarios.add(null);
				horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);
			}
		}
		
		return horarios;

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
		buscarFuncionarios();
		List<LocalTime> h;
		for (Usuario u : setFuncionarios) {
			h = new ArrayList<>();
			h.addAll(buscarHorarios(u));
			lista.add(new HorarioLivre(u.getNome(),h));
		}
	}

	public void buscarFuncionarios() {
		List<Usuario> funcionariosCorreto = new ArrayList<>();
		boolean primeiro = true;
		if (!servicosSelecionados.isEmpty()) {
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

			setFuncionarios.clear();
			setFuncionarios.addAll(funcionariosCorreto);
		} else {
			setFuncionarios.clear();
			setFuncionarios
					.addAll(Usuario.filtraPorRole(usuarioRepository.findByAtivoOrderByNome(true), "ROLE_FUNCIONARIO"));
		}
	}

	public List<LocalTime> buscarHorarios(Usuario funcionario) {
		if (!servicosSelecionados.isEmpty()) {
			tempoTotalServicos = LocalTime.of(0, 0, 0);
			for (Servico servico : servicosSelecionados) {
				tempoTotalServicos = somarLocalTime(tempoTotalServicos, servico.getTempo());
			}
			
			return horariosLivres(tempoTotalServicos, funcionario);
		} else {
			horarios.clear();
			horarios.addAll(buscarTodosHorarios(LocalTime.of(0, 0)));

			horarioAgendados = horarioAgendadoRepository.findByFuncionarioAndData(funcionario.getId(),
					getObjeto().getData());
			retirarHorariosOcupadosSemServico();
			return horarios;
		}

	}

	private List<LocalTime> horariosLivres(LocalTime TempoTotalServicos, Usuario funcionario) {
		horarios.clear();
		horarios.addAll(buscarTodosHorarios(TempoTotalServicos));

		horarioAgendados = horarioAgendadoRepository.findByFuncionarioAndData(funcionario.getId(),
				getObjeto().getData());
		retirarHorariosOcupadosComServico(TempoTotalServicos);

		return horarios;

	}

	private void retirarHorariosOcupadosSemServico() {

		LocalTime horaAuxiliar;
		if (!horarioAgendados.isEmpty()) {
			for (HorarioAgendado horarioAgendado : horarioAgendados) {
				horaAuxiliar = horarioAgendado.getHoraInicio();

				while (horaAuxiliar.isBefore(horarioAgendado.getHoraTermino())) {
					if (horarios.contains(horaAuxiliar))
						horarios.set(horarios.indexOf(horaAuxiliar), null);

					horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);

				}
			}

		}

	}

	private void retirarHorariosOcupadosComServico(LocalTime TempoTotalServicos) {

		LocalTime horaAuxiliar = HORA_INICIO_EMPRESA;
		if (!horarioAgendados.isEmpty()) {
			for (HorarioAgendado horarioAgendado : horarioAgendados) {
				if (verificaEspacoTempo(HORA_INICIO_EMPRESA, TempoTotalServicos,
						horarioAgendado.getHoraInicio()) == false
						&& horarioAgendado.getHoraInicio().isBefore(HORA_INICIO_INTERVALO)) {
					horaAuxiliar = HORA_INICIO_EMPRESA;
					while (horaAuxiliar.isBefore(horarioAgendado.getHoraTermino())) {
						if (horarios.contains(horaAuxiliar) && !horarios.get(horarios.indexOf(horaAuxiliar)).equals(null) )
							horarios.set(horarios.indexOf(horaAuxiliar), null);
						horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);
					}
				}
				if (verificaEspacoTempo(HORA_FINAL_INTERVALO, TempoTotalServicos,
						horarioAgendado.getHoraInicio()) == false
						&& horarioAgendado.getHoraInicio().isAfter(HORA_FINAL_INTERVALO)) {
					horaAuxiliar = HORA_FINAL_INTERVALO;
					while (horaAuxiliar.isBefore(horarioAgendado.getHoraTermino())) {
						horarios.set(horarios.indexOf(horaAuxiliar), null);
						horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);
					}
				} else {
					horaAuxiliar = subtrairLocalTime(horarioAgendado.getHoraInicio(), TempoTotalServicos);
					horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);
					while (horaAuxiliar.isBefore(horarioAgendado.getHoraTermino())) {
						if (horarios.contains(horaAuxiliar))
							horarios.set(horarios.indexOf(horaAuxiliar), null);
						horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);
					}
				}
			}
		}

	}

	private boolean verificaEspacoTempo(LocalTime primeiroHorarioLivre, LocalTime tempoTotalServico,
			LocalTime proximoServico) {
		LocalTime total = somarLocalTime(primeiroHorarioLivre, tempoTotalServico);
		if (tempoTotalServico.equals(LocalTime.of(0, 0))) {
			if (total.isBefore(proximoServico)) {
				return true;
			}

			return false;
		} else {
			if (total.isBefore(proximoServico) || total.equals(proximoServico)) {
				return true;
			}

			return false;
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

}