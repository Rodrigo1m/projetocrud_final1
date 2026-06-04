package com.grupotres.projetocrud.service;

import com.grupotres.projetocrud.model.*;
import com.grupotres.projetocrud.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ObraService {

    @Autowired private ObraRepository obraRepo;
    @Autowired private ObraTipoAtendimentoRepository otaRepo;
    @Autowired private TipoAtendimentoRepository tipoRepo;

    // ---- OBRAS ----

    @Transactional
    public Obra criarObra(String nome, String endereco, User cliente) {
        if (nome == null || nome.isBlank()) throw new RuntimeException("Nome é obrigatório!");
        Obra obra = new Obra(nome, endereco, cliente);
        return obraRepo.save(obra);
    }

    public List<Obra> listarObras() {
        return obraRepo.findAll();
    }

    public List<Obra> listarObrasPorCliente(User cliente) {
        return obraRepo.findByCliente(cliente);
    }

    public List<Obra> listarObrasPorMembro(User membro) {
        return obraRepo.findByEquipeContaining(membro);
    }

    @Transactional
    public Obra editarObra(Long id, String nome, String endereco, User cliente) {
        Obra obra = obraRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Obra não encontrada!"));
        obra.setNome(nome);
        obra.setEndereco(endereco);
        obra.setCliente(cliente);
        return obraRepo.save(obra);
    }

    @Transactional
    public void excluirObra(Long id) {
        obraRepo.deleteById(id);
    }

    // ---- EQUIPE DA OBRA ----

    @Transactional
    public void adicionarMembroEquipe(Long obraId, User membro) {
        Obra obra = obraRepo.findById(obraId)
            .orElseThrow(() -> new RuntimeException("Obra não encontrada!"));
        if (!obra.getEquipe().contains(membro)) {
            obra.getEquipe().add(membro);
            obraRepo.save(obra);
        }
    }

    @Transactional
    public void removerMembroEquipe(Long obraId, User membro) {
        Obra obra = obraRepo.findById(obraId)
            .orElseThrow(() -> new RuntimeException("Obra não encontrada!"));
        obra.getEquipe().remove(membro);
        obraRepo.save(obra);
    }

    // ---- TIPOS DE ATENDIMENTO DA OBRA ----

    @Transactional
    public ObraTipoAtendimento adicionarTipoAtendimento(Long obraId, Long tipoId) {
        Obra obra = obraRepo.findById(obraId)
            .orElseThrow(() -> new RuntimeException("Obra não encontrada!"));
        TipoAtendimento tipo = tipoRepo.findById(tipoId)
            .orElseThrow(() -> new RuntimeException("Tipo de atendimento não encontrado!"));
        ObraTipoAtendimento ota = new ObraTipoAtendimento(obra, tipo);
        return otaRepo.save(ota);
    }

    @Transactional
    public ObraTipoAtendimento alterarStatus(Long otaId, StatusAtendimento novoStatus) {
        ObraTipoAtendimento ota = otaRepo.findById(otaId)
            .orElseThrow(() -> new RuntimeException("Atendimento não encontrado!"));
        ota.alterarStatus(novoStatus);
        return otaRepo.save(ota);
    }

    @Transactional
    public void adicionarTerceirizada(Long otaId, User terceirizada) {
        ObraTipoAtendimento ota = otaRepo.findById(otaId)
            .orElseThrow(() -> new RuntimeException("Atendimento não encontrado!"));
        if (!ota.getTerceirizadas().contains(terceirizada)) {
            ota.getTerceirizadas().add(terceirizada);
            otaRepo.save(ota);
        }
    }

    @Transactional
    public void removerTerceirizada(Long otaId, User terceirizada) {
        ObraTipoAtendimento ota = otaRepo.findById(otaId)
            .orElseThrow(() -> new RuntimeException("Atendimento não encontrado!"));
        ota.getTerceirizadas().remove(terceirizada);
        otaRepo.save(ota);
    }

    public List<ObraTipoAtendimento> listarAtendimentosPorObra(Obra obra) {
        return otaRepo.findByObra(obra);
    }

    public List<ObraTipoAtendimento> listarAtendimentosPorTerceirizada(User user) {
        return otaRepo.findByTerceirizada(user);
    }

    public Optional<Obra> buscarPorId(Long id) {
        return obraRepo.findById(id);
    }

    // ---- TIPOS DE ATENDIMENTO GLOBAIS ----

    @Transactional
    public TipoAtendimento criarTipoAtendimento(String nome, String descricao) {
        if (nome == null || nome.isBlank()) throw new RuntimeException("Nome é obrigatório!");
        return tipoRepo.save(new TipoAtendimento(nome, descricao));
    }

    public List<TipoAtendimento> listarTiposAtendimento() {
        return tipoRepo.findAll();
    }

    @Transactional
    public TipoAtendimento editarTipoAtendimento(Long id, String nome, String descricao) {
        TipoAtendimento t = tipoRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Tipo não encontrado!"));
        t.setNome(nome);
        t.setDescricao(descricao);
        return tipoRepo.save(t);
    }

    @Transactional
    public void excluirTipoAtendimento(Long id) {
        tipoRepo.deleteById(id);
    }
}
