package com.grupotres.projetocrud.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "obra_tipos_atendimento")
public class ObraTipoAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "obra_id")
    private Obra obra;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_atendimento_id")
    private TipoAtendimento tipoAtendimento;

    @Enumerated(EnumType.STRING)
    private StatusAtendimento status = StatusAtendimento.ABERTO;

    // Terceirizadas designadas para este atendimento
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "obra_tipo_terceirizadas",
        joinColumns = @JoinColumn(name = "obra_tipo_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> terceirizadas = new ArrayList<>();

    // Histórico de status
    @OneToMany(mappedBy = "obraTipoAtendimento", cascade = CascadeType.ALL,
               fetch = FetchType.EAGER, orphanRemoval = true)
    @OrderBy("dataInicio ASC")
    private List<StatusHistorico> historico = new ArrayList<>();

    public ObraTipoAtendimento() {}

    public ObraTipoAtendimento(Obra obra, TipoAtendimento tipo) {
        this.obra            = obra;
        this.tipoAtendimento = tipo;
        this.status          = StatusAtendimento.ABERTO;
        registrarHistorico(StatusAtendimento.ABERTO);
    }

    public void alterarStatus(StatusAtendimento novoStatus) {
        this.status = novoStatus;
        registrarHistorico(novoStatus);
    }

    private void registrarHistorico(StatusAtendimento s) {
        StatusHistorico h = new StatusHistorico(this, s);
        historico.add(h);
    }

    public Long getId()                               { return id; }
    public Obra getObra()                             { return obra; }
    public TipoAtendimento getTipoAtendimento()       { return tipoAtendimento; }
    public StatusAtendimento getStatus()              { return status; }
    public void setStatus(StatusAtendimento s)        { this.status = s; }
    public List<User> getTerceirizadas()              { return terceirizadas; }
    public List<StatusHistorico> getHistorico()       { return historico; }
}
