package com.grupotres.projetocrud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grupotres.projetocrud.model.User;
import java.util.List;
import java.util.Scanner;
import java.util.Optional;

// vai receber o user  e enviar o objeto ao banco
// O banco usando GeneratedValue seta o ID
public interface UserRepository extends JpaRepository<User, Long> {

    // Criar método de busca
    List<User> findByNomeContainingIgnoreCase(String nome);    
    Optional<User> findByEmail(String email);
}