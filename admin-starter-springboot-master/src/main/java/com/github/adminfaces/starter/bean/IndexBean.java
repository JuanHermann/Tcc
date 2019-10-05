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

    private boolean isFuncionario;

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
    private List<Usuario> setFuncionarios;
    private List<Usuario> setFuncionariosBloqueio;
    private List<Usuario> funcionariosAgenda;
    private Usuario funcionario;
    private Usuario funcionarioDoList;
    private Usuario funcionarioDoBloqueio;
    private Usuario funcionarioDaAgenda;
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
        verificaPermissao();
        super.init();

        if (empresaRepository.findAll().size() > 0) {
            Empresa empresa = empresaRepository.findAll().get(0);
            HORA_INICIO_EMPRESA = empresa.getHoraAbertura();
            HORA_INICIO_INTERVALO = empresa.getInicioIntervalo();
            HORA_FINAL_INTERVALO = empresa.getFinalIntervalo();
            HORA_FINAL_EMPRESA = empresa.getHoraFechamento();
            TEMPO_BUSCA_ENTRE_SERVICOS = empresa.getTempoMinServico();
        }

        inicioSchedule = HORA_INICIO_EMPRESA.toString();
        finalSchedule = HORA_FINAL_EMPRESA.toString();
        tempoMinutosSchedule = TEMPO_BUSCA_ENTRE_SERVICOS.getMinute();

        inicioHoraCalendar = HORA_INICIO_EMPRESA.getHour();
        finalHoraCalendar = HORA_FINAL_EMPRESA.getHour();

        timeZoneBrasil = TimeZone.getTimeZone("America/Sao_Paulo");
        clientes = permissaoRepository.findByNome("ROLE_CLIENTE").getUsuarios();
        horarioAgendados = new ArrayList<>();
        servicos = servicoRepository.findByAtivoOrderByNome(true);
        servicos.remove(servicoRepository.findById(1).get());
        servicosSelecionados = new ArrayList<>();
        cliente = new Usuario();
        funcionario = new Usuario();
        funcionarioDoList = new Usuario();
        funcionarioDoBloqueio = new Usuario();
        funcionarioDaAgenda = new Usuario();
        funcionarios = new ArrayList<>();
        setFuncionarios = new ArrayList<>();
        setFuncionariosBloqueio = Usuario.filtraPorRole(usuarioRepository.findByAtivoOrderByNome(true),
                "ROLE_FUNCIONARIO");
        stringHorario = "Selecione um Horário";
        carregarFuncionariosDaAgenda();
        atualizarSchedule();
    }

    private void carregarFuncionariosDaAgenda() {
        funcionariosAgenda = new ArrayList<>();
        Usuario u = new Usuario();
        u.setNome("Todos");
        funcionariosAgenda.add(u);
        funcionariosAgenda
                .addAll(Usuario.filtraPorRole(usuarioRepository.findByAtivoOrderByNome(true), "ROLE_FUNCIONARIO"));

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
        } else if (Usuario.hasRole("ROLE_FUNCIONARIO", usuarioLogadoBean.getUsuario())) {
            tipoUsuario = "funcionario";
            isFuncionario = true;
        }

        if (tipoUsuario == "") {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("indexcliente.jsf");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void atualizarSchedule() {
        boolean maisFuncionario = mostrarFuncionario();
        if (maisFuncionario) {
            if (funcionarioDaAgenda.getId() == null) {
                horarioAgendados = horarioAgendadoRepository.findAll();
            } else {
                horarioAgendados = horarioAgendadoRepository.findByFuncionario(funcionarioDaAgenda.getId());
            }
        } else {
            horarioAgendados = horarioAgendadoRepository.findByFuncionario(usuarioLogadoBean.getUsuario().getId());
        }
        eventModel = new DefaultScheduleModel();
        Date dataInicio = new Date(), dataFinal = new Date();
        for (HorarioAgendado horario : horarioAgendados) {
            LocalDateTime localDataInicio = LocalDateTime.of(horario.getData(), horario.getHoraInicio());
            LocalDateTime localDataFinal = LocalDateTime.of(horario.getData(), horario.getHoraTermino());
            dataInicio = Date.from(localDataInicio.toInstant(ZoneOffset.UTC));
            dataFinal = Date.from(localDataFinal.toInstant(ZoneOffset.UTC));
            String titulo = "";
            if (horario.getCliente() == null) {
                titulo = "Bloqueio";
                if (maisFuncionario) {
                    titulo = titulo + " do " + horario.getUsuarioServico().getUsuario().getNome();
                }
                DefaultScheduleEvent df = new DefaultScheduleEvent(titulo, dataInicio, dataFinal, horario);
                df.setStyleClass("btn-danger");
                eventModel.addEvent(df);

            } else {
                titulo = horario.getCliente().getNome() + " - " + horario.getUsuarioServico().getServico().getNome();
                if (maisFuncionario) {
                    titulo = "F: " + horario.getUsuarioServico().getUsuario().getNome() + " - C: " + titulo;
                }
                eventModel.addEvent(new DefaultScheduleEvent(titulo, dataInicio, dataFinal, horario));
            }
        }
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
            stringHorario = "Nenhum Horário disponivel nesta Data";
        } else {
            stringHorario = "Selecione um horário";
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

    public boolean mostrarForm() {
        return "servico".equalsIgnoreCase(tipo);

    }

    public boolean mostrarFuncionario() {
        return permissaoRepository.findByNome("ROLE_FUNCIONARIO").getUsuarios().size() > 1 && isFuncionario == false;

    }

    public void excluirAgendamento() throws InstantiationException, IllegalAccessException {
        super.remover();
        atualizarSchedule();
        context.fecharDialog("inserir");
    }

    public void salvarAgendamento() {
        if (mostrarForm() == true) {
            if (funcionarioDoList.getId() == null) {
                pegarFuncionarioCorrespondenteAoHorario();
            }

            HorarioAgendado agendado = new HorarioAgendado();
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
                if (mostrarFuncionario()) {

                    agendado.setUsuarioServico(usuarioServicoRepository
                            .findByServicoAndUsuarioAndAtivoOrderByUsuario(servico, funcionarioDoList, true));

                } else {
                    agendado.setUsuarioServico(usuarioServicoRepository.findByServicoAndUsuarioAndAtivoOrderByUsuario(
                            servico, usuarioLogadoBean.getUsuario(), true));
                }
                getRepository().save(agendado);

            }
            setObjeto(new HorarioAgendado());
            if (mostrarFuncionario()) {
                funcionarioDoList = new Usuario();
            }
            servicosSelecionados.clear();
            atualizarSchedule();
            addDetailMessage("Cadastrado com sucesso");
            context.fecharDialog("inserir");

        } else {
            UsuarioServico us = new UsuarioServico();
            if (mostrarFuncionario()) {
                us = usuarioServicoRepository.findByServicoAndUsuario(servicoRepository.findById(1).get(),
                        funcionarioDoBloqueio);
            } else {
                us = usuarioServicoRepository.findByServicoAndUsuario(servicoRepository.findById(1).get(),
                        usuarioLogadoBean.getUsuario());

            }
            HorarioAgendado bloquear = new HorarioAgendado();
            LocalDate dataInicio = dataInicioBloqueio.toLocalDate(), dataFinal = dataFinalBloqueio.toLocalDate();
            if (getObjeto().getId() != null) {
                bloquear = getObjeto();
            }
            if (!dataInicio.isEqual(dataFinal)) {

                do {
                    if (bloquear.getId() == null) {
                        bloquear = new HorarioAgendado();
                    }
                    bloquear.setData(dataInicio);
                    bloquear.setUsuarioServico(us);
                    if (dataInicio.isEqual(dataInicioBloqueio.toLocalDate())) {
                        bloquear.setHoraInicio(dataInicioBloqueio.toLocalTime());
                    } else {

                        bloquear.setHoraInicio(HORA_INICIO_EMPRESA);
                    }

                    bloquear.setHoraTermino(HORA_FINAL_EMPRESA);

                    getRepository().save(bloquear);
                    bloquear = new HorarioAgendado();
                    dataInicio = dataInicio.plusDays(1L);

                } while (!dataInicio.isEqual(dataFinal));

            }

            bloquear.setData(dataInicio);
            bloquear.setUsuarioServico(us);

            if (dataInicio.isEqual(dataInicioBloqueio.toLocalDate())) {
                bloquear.setHoraInicio(dataInicioBloqueio.toLocalTime());
            } else {
                bloquear.setHoraInicio(HORA_INICIO_EMPRESA);
            }
            bloquear.setHoraTermino(dataFinalBloqueio.toLocalTime());

            getRepository().save(bloquear);

            setObjeto(new HorarioAgendado());
            atualizarSchedule();
            addDetailMessage("Bloqueio realizado com sucesso");
            context.fecharDialog("inserir");
            tipo = "servico";

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
        stringHorario = "Selecione um horario";
        servicosSelecionados = new ArrayList<>();
        event = (ScheduleEvent) selectEvent.getObject();
        setObjeto((HorarioAgendado) event.getData());
        if (getObjeto().getCliente() == null) {
            tipo = "bloqueio";
            funcionarioDoBloqueio = getObjeto().getUsuarioServico().getUsuario();
            dataInicioBloqueio = LocalDateTime.of(getObjeto().getData(), getObjeto().getHoraInicio());
            dataFinalBloqueio = LocalDateTime.of(getObjeto().getData(), getObjeto().getHoraTermino());
        } else {
            tipo = "servico";
            funcionarioDoList = getObjeto().getUsuarioServico().getUsuario();
            servicosSelecionados.add(getObjeto().getUsuarioServico().getServico());
            buscarHorarios();
            horarios.add(getObjeto().getHoraInicio());
        }

    }

    public void onDateSelect(SelectEvent selectEvent) {
        stringHorario = "Selecione um horario";
        setObjeto(new HorarioAgendado());
        servicosSelecionados = new ArrayList<>();
        funcionarioDaAgenda = new Usuario();
        funcionarioDoList = new Usuario();
        Date dataSelecionada = (Date) selectEvent.getObject();
        event = new DefaultScheduleEvent("", dataSelecionada, dataSelecionada);
        getObjeto().setData(dataSelecionada.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        dataInicioBloqueio = LocalDateTime.of(getObjeto().getData(), HORA_INICIO_EMPRESA);
        dataFinalBloqueio = LocalDateTime.of(getObjeto().getData(), HORA_FINAL_EMPRESA);
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