package com.grupotres.projetocrud.repository;

import com.grupotres.projetocrud.model.Obra;
import com.grupotres.projetocrud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ObraRepository extends JpaRepository<Obra, Long> {
    List<Obra> findByCliente(User cliente);
    List<Obra> findByEquipeContaining(User membro);
    List<Obra> findByNomeContainingIgnoreCase(String nome);
}
