package com.grupotres.projetocrud.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipos_atendimento")
public class TipoAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;

    public TipoAtendimento() {}

    public TipoAtendimento(String nome, String descricao) {
        this.nome      = nome;
        this.descricao = descricao;
    }

    public Long getId()             { return id; }
    public String getNome()         { return nome; }
    public void setNome(String n)   { this.nome = n; }
    public String getDescricao()    { return descricao; }
    public void setDescricao(String d){ this.descricao = d; }

    @Override public String toString() { return nome; }
}
