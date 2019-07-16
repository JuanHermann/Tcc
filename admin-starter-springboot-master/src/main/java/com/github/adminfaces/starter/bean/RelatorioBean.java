package com.github.adminfaces.starter.bean;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.github.adminfaces.starter.model.Empresa;
import com.github.adminfaces.starter.model.HorarioAgendado;
import com.github.adminfaces.starter.model.HorarioLivre;
import com.github.adminfaces.starter.model.Permissao;
import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.model.UsuarioServico;
import com.github.adminfaces.starter.report.SeguroReportService;
import com.github.adminfaces.starter.repository.EmpresaRepository;
import com.github.adminfaces.starter.repository.HorarioAgendadoRepository;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.ServicoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;
import com.github.adminfaces.starter.util.GerarRelatorio;

import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

@Component
@Scope("view")
@Getter
@Setter
public class RelatorioBean extends AbastractFormBean<HorarioAgendado, HorarioAgendadoRepository> {
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

	@Autowired
	private GerarRelatorio gerarRelatorio;

	private List<HorarioLivre> lista;
	private List<LocalTime> todosHorarios;

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
    private SeguroReportService seguroReportService;

	@Autowired
	private PermissaoRepository permissaoRepository;

	@Autowired
	private HorarioAgendadoRepository horarioAgendadoRepository;
	private List<HorarioAgendado> horarioAgendados;

	@Autowired
	private EmpresaRepository empresaRepository;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	@Autowired
	@Qualifier("jdbcTemplate")
	private JdbcTemplate jdbcTemplate;

	public RelatorioBean() {
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
		setFuncionarios = new ArrayList<>();

		lista = new ArrayList<>();
		try {
			gerar("classpath:/reports/ServicosProcurados.jrxml",);
		} catch (ClassNotFoundException | JRException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void gerar(String report,HttpServletResponse response) throws JRException , SQLException, ClassNotFoundException, IOException {
		
		String path = resourceLoader.getResource(report).getURI().getPath();

		JasperReport jasperReport = JasperCompileManager.compileReport(path);
		Connection conn = jdbcTemplate.getDataSource().getConnection();
      
		//executa o relatório
		Map parametros = new HashMap<>();
		parametros.put("nota", new Double(10));
		JasperPrint impressao = JasperFillManager.fillReport( jasperReport , parametros,    conn );
		gerarRelatorio.imprimir(response, impressao);
		//exibe o resultado
		JasperViewer viewer = new JasperViewer( impressao , true );
		viewer.show();
	}

	private String printReport() throws JRException, IOException {
	 String path = "classpath:/reports/servicosProcurados.jrxml";
	 InputStream jasperTemplate = FacesContext.getCurrentInstance().
	getExternalContext().getResourceAsStream(path);
	

	 JasperReport jasperReport = JasperCompileManager.compileReport(jasperTemplate);

//	    Map parametros = new HashMap();
//	    parametros.put("clienteDe", this.codigoClienteDe);
//	    parametros.put("clienteAte", this.codigoClienteAte);
	 
	      Connection conexao = null;
	      try {
	        Class.forName("com.postgresql.jdbc.driver");
	        conexao = DriverManager.getConnection("jdbc:postgresql://localhost:5432/tcc", "postgres", "1234");
	      } catch (Exception e) {
	        // TODO: handle exception
	      }

	 	JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,null,conexao);

	 HttpServletResponse response = (HttpServletResponse)
	FacesContext.getCurrentInstance().getExternalContext().getResponse();
	
 ServletOutputStream outputStream = response.getOutputStream();
	
 response.setContentType("application/pdf");
	 response.setHeader("Content-Disposition", "attachment; filename=\"relatorio.pdf\"");
	

	 JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
	 
	 outputStream.flush();
	 outputStream.close();
	 FacesContext.getCurrentInstance().renderResponse();
	 FacesContext.getCurrentInstance().responseComplete();
	 
	 return "";
	
	 }
	

   

    public void export(HttpServletResponse response) throws IOException, JRException, SQLException {
        String ordem = "";
        JasperPrint jasperPrint = seguroReportService.generatePromissoria(1L, "Relatorio de Seguros por Cliente", "clientePassa", "classpath:/reports/ClienteSeguroReport.jrxml", ordem, ordem);
//        if (botao.equalsIgnoreCase("mostrar")) {
//            gerarRelatorio.imprimir(response, jasperPrint);
//        } else if (botao.equalsIgnoreCase("baixar")) {
            gerarRelatorio.baixar("RelatorioCliente.pdf", response, jasperPrint);
//        }

    }

	private void buscarTodosHorarios() {
		todosHorarios = new ArrayList<>();
		List<LocalTime> horarios = new ArrayList<>();
		LocalTime TempoTotalServicos = LocalTime.of(0, 0);
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
		todosHorarios = horarios;

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
		for (Usuario u : setFuncionarios) {
			lista.add(new HorarioLivre(u.getNome(), buscarHorarios(u)));
		}

	}

	public void buscarFuncionarios() {
		List<Usuario> funcionariosCorreto = new ArrayList<>();
		boolean primeiro = true;
		setFuncionarios.clear();
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

				setFuncionarios.addAll(funcionariosCorreto);
			}
		} else {
			setFuncionarios.addAll(Usuario.filtraPorRole(usuarioRepository.findAll(), "ROLE_FUNCIONARIO"));
		}
	}

	public List<LocalTime> buscarHorarios(Usuario funcionario) {

		tempoTotalServicos = LocalTime.of(0, 0, 0);
		for (Servico servico : servicosSelecionados) {
			tempoTotalServicos = somarLocalTime(tempoTotalServicos, servico.getTempo());
		}
		System.out.println(tempoTotalServicos);
		return horariosLivres(tempoTotalServicos, funcionario);

	}

	private List<LocalTime> horariosLivres(LocalTime TempoTotalServicos, Usuario funcionario) {
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

		horarioAgendados = horarioAgendadoRepository.findByFuncionarioAndData(funcionario.getId(), LocalDate.now());
		retirarHorariosOcupados(TempoTotalServicos);

		return horarios;

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
						horarios.set(horarios.indexOf(horaAuxiliar), LocalTime.of(0, 0));
						horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);
					}
				}
				if (verificaEspacoTempo(HORA_FINAL_INTERVALO, TempoTotalServicos,
						horarioAgendado.getHoraInicio()) == false
						&& horarioAgendado.getHoraInicio().isAfter(HORA_FINAL_INTERVALO)) {
					horaAuxiliar = HORA_FINAL_INTERVALO;
					while (horaAuxiliar.isBefore(horarioAgendado.getHoraTermino())) {
						horarios.set(horarios.indexOf(horaAuxiliar), LocalTime.of(0, 0));
						horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);
					}
				} else {
					horaAuxiliar = subtrairLocalTime(horarioAgendado.getHoraInicio(), TempoTotalServicos);
					horaAuxiliar = somarLocalTime(horaAuxiliar, TEMPO_BUSCA_ENTRE_SERVICOS);
					while (horaAuxiliar.isBefore(horarioAgendado.getHoraTermino())) {
						horarios.set(horarios.indexOf(horaAuxiliar), LocalTime.of(0, 0));
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