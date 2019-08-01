package com.github.adminfaces.starter.bean;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.adminfaces.starter.model.HorarioAgendado;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.report.RelatorioServicosPrestados;
import com.github.adminfaces.starter.repository.HorarioAgendadoRepository;

import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;

@Component
@Scope("view")
@Getter
@Setter
public class RelatorioBean extends AbastractFormBean<HorarioAgendado, HorarioAgendadoRepository> {

	private static String relatorioA = "Numero de Serviços Prestados";
	private static String relatorioB = "Horários Preferidos";
	private static String relatorioC = "Entrada Mensal";
	private static String relatorioD = "Serviços Realizados";

	private boolean isFuncionario;

	private LocalDate dataInicio;
	private LocalDate dataFinal;

	private String relatorio;
	private List<String> relatorios;

	@Autowired
	private UsuarioLogadoBean usuarioLogadoBean;

	private RelatorioServicosPrestados relatorioServicosPrestados;

	public RelatorioBean() {
		super(HorarioAgendado.class);

	}

	@PostConstruct
	public void init() throws InstantiationException, IllegalAccessException {
		verificaPermissao();
		super.init();
		relatorios = new ArrayList<>();
		relatorios.add(relatorioA);
		relatorios.add(relatorioB);
		relatorios.add(relatorioC);
		relatorios.add(relatorioD);

	}

	public void abrirRelatorio() throws IOException {
		LocalDate d = LocalDate.now();
		if (dataInicio == null) {
			dataInicio = LocalDate.of(d.getYear(), d.getMonth(), 1);
		}
		if (dataFinal == null) {
			dataFinal = d;
		}
		if (relatorio.equals(relatorioA)) {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("relatorio/servicos?data1=" + dataInicio + "&data2=" + dataFinal);
		} else if (relatorio.equals(relatorioB)) {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("relatorio/horarios?data1=" + dataInicio + "&data2=" + dataFinal);
		} else if (relatorio.equals(relatorioC)) {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("relatorio/entradas?data1=" + dataInicio + "&data2=" + dataFinal);
		} else if (relatorio.equals(relatorioD)) {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect("relatorio/funcionarios?data1=" + dataInicio + "&data2=" + dataFinal);
		}
	}

	public void executeReport() {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

		InputStream reportStream = facesContext.getExternalContext()
				.getResourceAsStream("/reports/ServicosProcurados.jasper");

		response.setContentType("application/pdf");

		response.setHeader("Content-disposition", "inline;filename=relatorio.pdf");

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("TITULO", "titulo");

		try {
			ServletOutputStream servletOutputStream = response.getOutputStream();

			JasperRunManager.runReportToPdfStream(reportStream, servletOutputStream, parameters);

			servletOutputStream.flush();
			servletOutputStream.close();
		} catch (JRException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			facesContext.responseComplete();
		}
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

}