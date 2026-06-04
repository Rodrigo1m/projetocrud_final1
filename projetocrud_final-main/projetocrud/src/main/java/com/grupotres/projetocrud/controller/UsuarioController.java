package com.grupotres.projetocrud.controller;

import com.grupotres.projetocrud.model.User;
import com.grupotres.projetocrud.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    public Optional<User> realizarLogin(String email, String senha) {
        return usuarioService.login(email, senha);
    }

    public User cadastrarUsuario(String nome, String email, String senha, String perfil) {
        return usuarioService.cadastrar(nome, email, senha, perfil);
    }

    public List<User> listarUsuarios() {
        return usuarioService.listarTodos();
    }

    public List<User> listarPorPerfil(String perfil) {
        return usuarioService.listarPorPerfil(perfil);
    }

    public void editarUsuario(User user, String novaSenhaTexto) {
        usuarioService.editar(user, novaSenhaTexto);
    }

    public void excluirUsuario(Long id) {
        usuarioService.excluir(id);
    }
}
