package com.grupotres.projetocrud.controller;

import com.grupotres.projetocrud.model.*;
import com.grupotres.projetocrud.service.ObraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ObraController {

    @Autowired private ObraService obraService;

    public Obra criarObra(String nome, String endereco, User cliente) {
        return obraService.criarObra(nome, endereco, cliente);
    }

    public List<Obra> listarObras() {
        return obraService.listarObras();
    }

    public List<Obra> listarObrasPorCliente(User cliente) {
        return obraService.listarObrasPorCliente(cliente);
    }

    public List<Obra> listarObrasPorMembro(User membro) {
        return obraService.listarObrasPorMembro(membro);
    }

    public Obra editarObra(Long id, String nome, String endereco, User cliente) {
        return obraService.editarObra(id, nome, endereco, cliente);
    }

    public void excluirObra(Long id) {
        obraService.excluirObra(id);
    }

    public void adicionarMembro(Long obraId, User membro) {
        obraService.adicionarMembroEquipe(obraId, membro);
    }

    public void removerMembro(Long obraId, User membro) {
        obraService.removerMembroEquipe(obraId, membro);
    }

    public ObraTipoAtendimento adicionarTipoAtendimento(Long obraId, Long tipoId) {
        return obraService.adicionarTipoAtendimento(obraId, tipoId);
    }

    public ObraTipoAtendimento alterarStatus(Long otaId, StatusAtendimento status) {
        return obraService.alterarStatus(otaId, status);
    }

    public void adicionarTerceirizada(Long otaId, User t) {
        obraService.adicionarTerceirizada(otaId, t);
    }

    public void removerTerceirizada(Long otaId, User t) {
        obraService.removerTerceirizada(otaId, t);
    }

    public List<ObraTipoAtendimento> listarAtendimentosPorObra(Obra obra) {
        return obraService.listarAtendimentosPorObra(obra);
    }

    public List<ObraTipoAtendimento> listarAtendimentosPorTerceirizada(User u) {
        return obraService.listarAtendimentosPorTerceirizada(u);
    }

    public Optional<Obra> buscarPorId(Long id) {
        return obraService.buscarPorId(id);
    }

    // Tipos globais
    public TipoAtendimento criarTipo(String nome, String descricao) {
        return obraService.criarTipoAtendimento(nome, descricao);
    }

    public List<TipoAtendimento> listarTipos() {
        return obraService.listarTiposAtendimento();
    }

    public TipoAtendimento editarTipo(Long id, String nome, String descricao) {
        return obraService.editarTipoAtendimento(id, nome, descricao);
    }

    public void excluirTipo(Long id) {
        obraService.excluirTipoAtendimento(id);
    }
}
