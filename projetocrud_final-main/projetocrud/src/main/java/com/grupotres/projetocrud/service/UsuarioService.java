package com.grupotres.projetocrud.service;

import com.grupotres.projetocrud.model.User;
import com.grupotres.projetocrud.repository.UserRepository;
import com.grupotres.projetocrud.util.PerfilUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Optional<User> login(String email, String senha) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (encoder.matches(senha, user.getSenha())) return Optional.of(user);
        }
        return Optional.empty();
    }

    public User cadastrar(String nome, String email, String senha, String perfil) {
        if (nome == null || nome.isBlank())   throw new RuntimeException("Nome é obrigatório!");
        if (email == null || email.isBlank())  throw new RuntimeException("Email é obrigatório!");
        if (senha == null || senha.isBlank())  throw new RuntimeException("Senha é obrigatória!");
        if (perfil == null || perfil.isBlank() || perfil.equals("Selecione o Perfil"))
            throw new RuntimeException("Perfil é obrigatório!");
        if (!PerfilUtil.TODOS_PERFIS.contains(perfil))
            throw new RuntimeException("Perfil inválido!");
        if (userRepository.findByEmail(email).isPresent())
            throw new RuntimeException("Email já cadastrado!");

        String senhaHash = encoder.encode(senha);
        User user = new User(nome, email, senhaHash, perfil);
        return userRepository.save(user);
    }

    public List<User> listarTodos() {
        return userRepository.findAll();
    }

    public List<User> listarPorPerfil(String perfil) {
        return userRepository.findAll().stream()
            .filter(u -> perfil.equalsIgnoreCase(u.getPerfil()) || u.getRoles().contains(perfil))
            .toList();
    }

    public User editar(User user, String novaSenhaTexto) {
        if (novaSenhaTexto == null || novaSenhaTexto.isBlank())
            throw new RuntimeException("Senha é obrigatória!");
        user.setSenha(encoder.encode(novaSenhaTexto));
        return userRepository.save(user);
    }

    public void excluir(Long id) {
        userRepository.deleteById(id);
    }
}
