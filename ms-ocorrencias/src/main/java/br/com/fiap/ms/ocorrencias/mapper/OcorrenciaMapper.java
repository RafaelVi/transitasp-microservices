package br.com.fiap.ms.ocorrencias.mapper;

import br.com.fiap.ms.ocorrencias.client.dto.LinhaClientResponseDTO;
import br.com.fiap.ms.ocorrencias.dto.request.OcorrenciaRequestDTO;
import br.com.fiap.ms.ocorrencias.dto.request.OcorrenciaUpdateRequestDTO;
import br.com.fiap.ms.ocorrencias.dto.response.LinhaResumoResponseDTO;
import br.com.fiap.ms.ocorrencias.dto.response.OcorrenciaDetalheResponseDTO;
import br.com.fiap.ms.ocorrencias.dto.response.OcorrenciaResponseDTO;
import br.com.fiap.ms.ocorrencias.entities.Ocorrencia;
import org.springframework.stereotype.Component;

/** Converte entre a entidade e os DTOs, para que a entidade nunca seja exposta na API. */
@Component
public class OcorrenciaMapper {

    public Ocorrencia toEntity(OcorrenciaRequestDTO request) {
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setLinhaId(request.linhaId());
        ocorrencia.setTipo(request.tipo());
        ocorrencia.setDescricao(request.descricao().trim());
        ocorrencia.setLocal(request.local().trim());
        ocorrencia.setImpacto(request.impacto());
        return ocorrencia;
    }

    public void copiarDados(OcorrenciaUpdateRequestDTO request, Ocorrencia ocorrencia) {
        ocorrencia.setTipo(request.tipo());
        ocorrencia.setDescricao(request.descricao().trim());
        ocorrencia.setLocal(request.local().trim());
        ocorrencia.setImpacto(request.impacto());
    }

    public OcorrenciaResponseDTO toResponse(Ocorrencia ocorrencia) {
        return new OcorrenciaResponseDTO(
                ocorrencia.getId(),
                ocorrencia.getLinhaId(),
                ocorrencia.getTipo(),
                ocorrencia.getDescricao(),
                ocorrencia.getLocal(),
                ocorrencia.getImpacto(),
                ocorrencia.getStatus(),
                ocorrencia.getAbertaEm(),
                ocorrencia.getEncerradaEm());
    }

    /** Ocorrência com os dados da linha retornados pelo ms-linhas. */
    public OcorrenciaDetalheResponseDTO toDetalhe(Ocorrencia ocorrencia, LinhaClientResponseDTO linha) {
        LinhaResumoResponseDTO resumo = new LinhaResumoResponseDTO(
                linha.id(),
                linha.numero(),
                linha.nome(),
                linha.status() == null ? null : linha.status().name());
        return new OcorrenciaDetalheResponseDTO(toResponse(ocorrencia), resumo, true, null);
    }

    /** Resposta degradada: usada quando não foi possível consultar a linha. */
    public OcorrenciaDetalheResponseDTO toDetalheSemLinha(Ocorrencia ocorrencia, String aviso) {
        return new OcorrenciaDetalheResponseDTO(toResponse(ocorrencia), null, false, aviso);
    }
}
