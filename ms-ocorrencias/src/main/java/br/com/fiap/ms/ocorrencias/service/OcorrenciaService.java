package br.com.fiap.ms.ocorrencias.service;

import br.com.fiap.ms.ocorrencias.client.LinhaClient;
import br.com.fiap.ms.ocorrencias.client.dto.AlterarStatusLinhaRequestDTO;
import br.com.fiap.ms.ocorrencias.client.dto.LinhaClientResponseDTO;
import br.com.fiap.ms.ocorrencias.client.dto.StatusLinha;
import br.com.fiap.ms.ocorrencias.dto.request.OcorrenciaRequestDTO;
import br.com.fiap.ms.ocorrencias.dto.request.OcorrenciaUpdateRequestDTO;
import br.com.fiap.ms.ocorrencias.dto.response.OcorrenciaDetalheResponseDTO;
import br.com.fiap.ms.ocorrencias.dto.response.OcorrenciaResponseDTO;
import br.com.fiap.ms.ocorrencias.entities.ImpactoLinha;
import br.com.fiap.ms.ocorrencias.entities.Ocorrencia;
import br.com.fiap.ms.ocorrencias.entities.StatusOcorrencia;
import br.com.fiap.ms.ocorrencias.exceptions.ResourceNotFoundException;
import br.com.fiap.ms.ocorrencias.exceptions.BusinessRuleException;
import br.com.fiap.ms.ocorrencias.exceptions.ServiceUnavailableException;
import br.com.fiap.ms.ocorrencias.mapper.OcorrenciaMapper;
import br.com.fiap.ms.ocorrencias.repository.OcorrenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Regras de negócio das ocorrências.
 *
 * Regra central: o status de uma linha (no ms-linhas) é sempre o impacto mais grave
 * entre as ocorrências ABERTAS dela; sem ocorrência aberta, a linha volta para NORMAL.
 * Toda operação que muda esse resultado chama o ms-linhas de forma síncrona (OpenFeign).
 * Se a chamada falhar, a transação local é desfeita, para os dois serviços não divergirem.
 */
@Service
public class OcorrenciaService {

    private final OcorrenciaRepository repository;
    private final OcorrenciaMapper mapper;
    private final LinhaClient linhaClient;

    public OcorrenciaService(OcorrenciaRepository repository, OcorrenciaMapper mapper, LinhaClient linhaClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.linhaClient = linhaClient;
    }

    @Transactional(readOnly = true)
    public List<OcorrenciaResponseDTO> listar(StatusOcorrencia status, Long linhaId) {
        List<Ocorrencia> ocorrencias;
        if (status != null && linhaId != null) {
            ocorrencias = repository.findByLinhaIdAndStatusOrderByAbertaEmDesc(linhaId, status);
        } else if (status != null) {
            ocorrencias = repository.findByStatusOrderByAbertaEmDesc(status);
        } else if (linhaId != null) {
            ocorrencias = repository.findByLinhaIdOrderByAbertaEmDesc(linhaId);
        } else {
            ocorrencias = repository.findAllByOrderByAbertaEmDesc();
        }
        return ocorrencias.stream().map(mapper::toResponse).toList();
    }

    /**
     * Devolve a ocorrência com a situação atual da linha, consultada no ms-linhas.
     * Se o ms-linhas não responder, o fallback entra em ação e a ocorrência é
     * devolvida mesmo assim, sem os dados da linha e com um aviso.
     */
    public OcorrenciaDetalheResponseDTO buscarPorId(Long id) {
        Ocorrencia ocorrencia = buscarEntidade(id);
        try {
            LinhaClientResponseDTO linha = linhaClient.buscarPorId(ocorrencia.getLinhaId());
            return mapper.toDetalhe(ocorrencia, linha);
        } catch (ServiceUnavailableException | ResourceNotFoundException ex) {
            return mapper.toDetalheSemLinha(ocorrencia, ex.getMessage());
        }
    }

    @Transactional
    public OcorrenciaDetalheResponseDTO criar(OcorrenciaRequestDTO request) {
        // Confere no ms-linhas se a linha existe (404 se não existir, 503 se estiver fora do ar)
        linhaClient.buscarPorId(request.linhaId());

        Ocorrencia ocorrencia = mapper.toEntity(request);
        ocorrencia.setStatus(StatusOcorrencia.ABERTA);
        ocorrencia.setAbertaEm(LocalDateTime.now());
        ocorrencia = repository.save(ocorrencia);

        LinhaClientResponseDTO linha = sincronizarStatusDaLinha(ocorrencia.getLinhaId());
        return mapper.toDetalhe(ocorrencia, linha);
    }

    @Transactional
    public OcorrenciaDetalheResponseDTO atualizar(Long id, OcorrenciaUpdateRequestDTO request) {
        Ocorrencia ocorrencia = buscarEntidade(id);
        exigirAberta(ocorrencia, "atualizada");

        mapper.copiarDados(request, ocorrencia);
        ocorrencia = repository.save(ocorrencia);

        // O impacto pode ter mudado, então o status da linha é recalculado
        LinhaClientResponseDTO linha = sincronizarStatusDaLinha(ocorrencia.getLinhaId());
        return mapper.toDetalhe(ocorrencia, linha);
    }

    @Transactional
    public OcorrenciaDetalheResponseDTO encerrar(Long id) {
        Ocorrencia ocorrencia = buscarEntidade(id);
        exigirAberta(ocorrencia, "encerrada novamente");

        ocorrencia.setStatus(StatusOcorrencia.ENCERRADA);
        ocorrencia.setEncerradaEm(LocalDateTime.now());
        ocorrencia = repository.save(ocorrencia);

        LinhaClientResponseDTO linha = sincronizarStatusDaLinha(ocorrencia.getLinhaId());
        return mapper.toDetalhe(ocorrencia, linha);
    }

    @Transactional
    public void excluir(Long id) {
        Ocorrencia ocorrencia = buscarEntidade(id);
        boolean estavaAberta = ocorrencia.getStatus() == StatusOcorrencia.ABERTA;

        repository.delete(ocorrencia);

        // Só uma ocorrência aberta influencia o status da linha
        if (estavaAberta) {
            sincronizarStatusDaLinha(ocorrencia.getLinhaId());
        }
    }

    /**
     * Calcula o status que a linha deve ter (pior impacto entre as ocorrências abertas,
     * ou NORMAL se não houver nenhuma) e envia ao ms-linhas.
     */
    private LinhaClientResponseDTO sincronizarStatusDaLinha(Long linhaId) {
        // Garante que a consulta abaixo já enxerga o que foi salvo ou excluído nesta transação
        repository.flush();

        StatusLinha novoStatus = repository
                .findByLinhaIdAndStatusOrderByAbertaEmDesc(linhaId, StatusOcorrencia.ABERTA)
                .stream()
                .map(Ocorrencia::getImpacto)
                .max(Comparator.naturalOrder())
                .map(this::paraStatusLinha)
                .orElse(StatusLinha.NORMAL);

        return linhaClient.alterarStatus(linhaId, new AlterarStatusLinhaRequestDTO(novoStatus));
    }

    private StatusLinha paraStatusLinha(ImpactoLinha impacto) {
        return StatusLinha.valueOf(impacto.name());
    }

    private void exigirAberta(Ocorrencia ocorrencia, String acao) {
        if (ocorrencia.getStatus() != StatusOcorrencia.ABERTA) {
            throw new BusinessRuleException(
                    "A ocorrência " + ocorrencia.getId() + " já está encerrada e não pode ser " + acao);
        }
    }

    private Ocorrencia buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência com id " + id + " não encontrada"));
    }
}
