package com.github.adminfaces.starter.report;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.github.adminfaces.starter.report.ReportHelper.FORMATO_RELATORIO;

public abstract class ReportItem {

	private String nome;
	private String descricao;
	private String jasperName;

	public ReportItem(String nome, String descricao, String jasperName) {
		this.nome = nome;
		this.descricao = descricao;
		this.jasperName = jasperName;
	}

	public ReportItem(String descricao) {
		this.nome = this.getClass().getSimpleName();
		this.descricao = descricao;
		this.jasperName = this.getClass().getSimpleName().toLowerCase();
	}

	public abstract void gerarRelatorio(FORMATO_RELATORIO formato);

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getJasperame() {
		return jasperName;
	}

	public void setJasperName(String jasperName) {
		this.jasperName = jasperName;
	}

}
