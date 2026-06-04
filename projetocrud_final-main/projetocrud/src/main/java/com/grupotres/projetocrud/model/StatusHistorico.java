package com.grupotres.projetocrud.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "obra_tipo_status_historico")
public class StatusHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obra_tipo_id")
    private ObraTipoAtendimento obraTipoAtendimento;

    @Enumerated(EnumType.STRING)
    private StatusAtendimento status;

    private String dataInicio;

    public StatusHistorico() {}

    public StatusHistorico(ObraTipoAtendimento ota, StatusAtendimento status) {
        this.obraTipoAtendimento = ota;
        this.status   = status;
        this.dataInicio = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public Long getId()                              { return id; }
    public StatusAtendimento getStatus()             { return status; }
    public String getDataInicio()                    { return dataInicio; }
    public ObraTipoAtendimento getObraTipoAtendimento(){ return obraTipoAtendimento; }
}
