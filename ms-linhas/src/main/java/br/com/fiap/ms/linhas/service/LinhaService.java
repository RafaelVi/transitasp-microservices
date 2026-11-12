package br.com.fiap.ms.linhas.service;

import br.com.fiap.ms.linhas.dto.request.AlterarStatusRequestDTO;
import br.com.fiap.ms.linhas.dto.request.LinhaRequestDTO;
import br.com.fiap.ms.linhas.dto.response.LinhaResponseDTO;
import br.com.fiap.ms.linhas.entities.Linha;
import br.com.fiap.ms.linhas.entities.StatusLinha;
import br.com.fiap.ms.linhas.exceptions.ResourceNotFoundException;
import br.com.fiap.ms.linhas.exceptions.BusinessRuleException;
import br.com.fiap.ms.linhas.mapper.LinhaMapper;
import br.com.fiap.ms.linhas.repository.LinhaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Regras de negócio das linhas. As controllers só falam com esta classe. */
@Service
public class LinhaService {

    private final LinhaRepository repository;
    private final LinhaMapper mapper;

    public LinhaService(LinhaRepository repository, LinhaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<LinhaResponseDTO> listar(StatusLinha status) {
        List<Linha> linhas = (status == null)
                ? repository.findAllByOrderByNumeroAsc()
                : repository.findByStatusOrderByNumeroAsc(status);
        return linhas.stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LinhaResponseDTO buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public LinhaResponseDTO criar(LinhaRequestDTO request) {
        if (repository.existsByNumero(request.numero())) {
            throw new BusinessRuleException("Já existe uma linha com o número " + request.numero());
        }
        Linha linha = mapper.toEntity(request);
        // Toda linha nasce em operação normal; o status só muda por ocorrências
        linha.setStatus(StatusLinha.NORMAL);
        linha.setAtualizadaEm(LocalDateTime.now());
        return mapper.toResponse(repository.save(linha));
    }

    @Transactional
    public LinhaResponseDTO atualizar(Long id, LinhaRequestDTO request) {
        Linha linha = buscarEntidade(id);
        if (repository.existsByNumeroAndIdNot(request.numero(), id)) {
            throw new BusinessRuleException("Já existe outra linha com o número " + request.numero());
        }
        mapper.copiarDados(request, linha);
        linha.setAtualizadaEm(LocalDateTime.now());
        return mapper.toResponse(repository.save(linha));
    }

    @Transactional
    public void excluir(Long id) {
        Linha linha = buscarEntidade(id);
        if (linha.getStatus() != StatusLinha.NORMAL) {
            throw new BusinessRuleException(
                    "A linha " + linha.getNumero() + " tem ocorrência aberta (status " + linha.getStatus()
                            + "). Encerre as ocorrências antes de excluir");
        }
        repository.delete(linha);
    }

    /** Chamado pelo ms-ocorrencias (OpenFeign) quando uma ocorrência é aberta, alterada ou encerrada. */
    @Transactional
    public LinhaResponseDTO alterarStatus(Long id, AlterarStatusRequestDTO request) {
        Linha linha = buscarEntidade(id);
        linha.setStatus(request.status());
        linha.setAtualizadaEm(LocalDateTime.now());
        return mapper.toResponse(repository.save(linha));
    }

    private Linha buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Linha com id " + id + " não encontrada"));
    }
}
