package com.grupotres.projetocrud.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;

    // Papel principal (compatibilidade legada)
    private String perfil;

    // Papéis do novo sistema
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<String> roles = new ArrayList<>();

    private String status;
    private String dataCadastro;

    public User() {}

    public User(String nome, String email, String senha, String perfil) {
        this.nome  = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
        this.dataCadastro = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.status = "Ativo";
        if (!this.roles.contains(perfil)) this.roles.add(perfil);
    }

    // ---- Getters / Setters ----
    public Long getId()                  { return id; }
    public String getNome()              { return nome; }
    public void setNome(String nome)     { this.nome = nome; }
    public String getEmail()             { return email; }
    public void setEmail(String email)   { this.email = email; }
    public String getSenha()             { return senha; }
    public void setSenha(String senha)   { this.senha = senha; }
    public String getPerfil()            { return perfil; }
    public String getStatus()            { return status; }
    public String getDataCadastro()      { return dataCadastro; }
    public List<String> getRoles()       { return roles; }
    public void setRoles(List<String> r) { this.roles = r; }

    public void definirPerfil(String perfil) {
        this.perfil = perfil;
        if (!this.roles.contains(perfil)) this.roles.add(perfil);
    }

    // ---- Verificações de papel ----
    public boolean isAdministrador() {
        return hasRole("Administrador") || hasRole("ADMIN MORHAR");
    }
    public boolean isResponsavelTecnico() {
        return hasRole("Responsável Técnico") || hasRole("TRABALHADOR INTERNO MORHAR");
    }
    public boolean isCliente() {
        return hasRole("Cliente") || hasRole("CLIENTE");
    }
    public boolean isTerceirizada() {
        return hasRole("Terceirizada") || hasRole("EMPRESA TERCERIZADA (PRESTADOR DE SERVIÇO)");
    }
    public boolean isEquipe() {
        return hasRole("Equipe");
    }
    public boolean isPodeEditar() {
        return isAdministrador();
    }
    public boolean isEquipeMorhar() {
        return isAdministrador() || isResponsavelTecnico() || isEquipe();
    }

    private boolean hasRole(String role) {
        if (roles == null) return false;
        return roles.stream().anyMatch(r -> r.equalsIgnoreCase(role));
    }

    public void ativarUsuario()    { this.status = "Ativo"; }
    public void desativarUsuario() { this.status = "Inativo"; }

    public String exibirInformacoes() {
        return """
            ===== DADOS DO USUÁRIO =====
            ID: %d | Nome: %s | Email: %s
            Perfil: %s | Status: %s | Cadastro: %s
            """.formatted(id, nome, email, perfil, status, dataCadastro);
    }
}
