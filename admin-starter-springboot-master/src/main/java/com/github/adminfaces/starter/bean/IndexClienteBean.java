package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.model.HorarioAgendado;
import com.github.adminfaces.starter.model.Servico;
import com.github.adminfaces.starter.model.Usuario;
import com.github.adminfaces.starter.repository.HorarioAgendadoRepository;
import com.github.adminfaces.starter.repository.PermissaoRepository;
import com.github.adminfaces.starter.repository.UsuarioRepository;
import com.github.adminfaces.starter.repository.UsuarioServicoRepository;
import com.github.adminfaces.starter.service.UsuarioService;

import lombok.Getter;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

import java.time.LocalTime;

import org.omnifaces.util.Faces;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Getter
@Scope("view")
public class IndexClienteBean extends AbastractFormBean<HorarioAgendado, HorarioAgendadoRepository> {

	@Autowired
	private UsuarioLogadoBean usuarioLogadoBean;
	@Autowired
	private HorarioAgendadoRepository horarioAgendadoRepository;
	@Autowired
	private PermissaoRepository permissaoRepository;
	@Autowired
	private UsuarioServicoRepository usuarioServicoRepository;

	private MenuModel menuModel;

	public IndexClienteBean() {
		super(Usuario.class);
	}

	@Override
	public void init() throws InstantiationException, IllegalAccessException {
		setId(usuarioLogadoBean.getUsuario().getId());
		menuModel = new DefaultMenuModel();
		for (HorarioAgendado horario : horarioAgendadoRepository
				.findByClienteOrderByHoraInicio(usuarioLogadoBean.getUsuario())) {

			DefaultSubMenu submenu = new DefaultSubMenu(horario.getData().toString() + " - "
					+ horario.getHoraInicio().toString() + " - " + horario.getUsuarioServico().getServico().getNome()); // Cria
																														// o
																														// submenu

			DefaultMenuItem item = new DefaultMenuItem(); // Cria o menuitem

			item.setValue(horario.getHoraTermino() + horario.getUsuarioServico().getUsuario().getNome());
			item.setStyleClass("btn-info");
			submenu.addElement(item); // adiciona o menuitem ao submenu

			menuModel.addElement(submenu); // adiciona o submenu ao menu
		}
		super.init();
	}
	public boolean mostrarFuncionario() {
		return permissaoRepository.findByNome("ROLE_FUNCIONARIO").getUsuarios().size() > 1;

	}

	public void salvarAgendamento() {
			if (mostrarFuncionario()) {
				for (Servico servico : servicosSelecionados) {
					getObjeto().setUsuarioServico(
							usuarioServicoRepository.findByServicoAndUsuarioOrderByUsuario(servico, funcionario));
				}
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
						agendado.setUsuarioServico(usuarioServicoRepository.findByServico(servico));
						getRepository().save(agendado);

					}
					setObjeto(new HorarioAgendado());
					servicosSelecionados.clear();
					atualizarSchedule();

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
					servicosSelecionados.clear();
					atualizarSchedule();
				}
			}
		

	}

}