package com.grupotres.projetocrud.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "obras")
public class Obra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String endereco;
    private String dataCadastro;

    // Cliente responsável
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    private User cliente;

    // Membros internos (Equipe)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "obra_equipe",
        joinColumns = @JoinColumn(name = "obra_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> equipe = new ArrayList<>();

    // Tipos de atendimento desta obra
    @OneToMany(mappedBy = "obra", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<ObraTipoAtendimento> tiposAtendimento = new ArrayList<>();

    public Obra() {}

    public Obra(String nome, String endereco, User cliente) {
        this.nome       = nome;
        this.endereco   = endereco;
        this.cliente    = cliente;
        this.dataCadastro = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public Long getId()          { return id; }
    public String getNome()      { return nome; }
    public void setNome(String n){ this.nome = n; }
    public String getEndereco()  { return endereco; }
    public void setEndereco(String e){ this.endereco = e; }
    public String getDataCadastro(){ return dataCadastro; }
    public User getCliente()     { return cliente; }
    public void setCliente(User c){ this.cliente = c; }
    public List<User> getEquipe(){ return equipe; }
    public List<ObraTipoAtendimento> getTiposAtendimento(){ return tiposAtendimento; }
}
