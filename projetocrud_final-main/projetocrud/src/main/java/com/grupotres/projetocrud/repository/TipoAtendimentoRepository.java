package com.grupotres.projetocrud.repository;

import com.grupotres.projetocrud.model.TipoAtendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TipoAtendimentoRepository extends JpaRepository<TipoAtendimento, Long> {
    List<TipoAtendimento> findByNomeContainingIgnoreCase(String nome);
}
