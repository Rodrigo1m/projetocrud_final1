package com.grupotres.projetocrud.repository;

import com.grupotres.projetocrud.model.ObraTipoAtendimento;
import com.grupotres.projetocrud.model.Obra;
import com.grupotres.projetocrud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ObraTipoAtendimentoRepository extends JpaRepository<ObraTipoAtendimento, Long> {
    List<ObraTipoAtendimento> findByObra(Obra obra);

    @Query("SELECT ota FROM ObraTipoAtendimento ota JOIN ota.terceirizadas t WHERE t = :user")
    List<ObraTipoAtendimento> findByTerceirizada(@Param("user") User user);
}
