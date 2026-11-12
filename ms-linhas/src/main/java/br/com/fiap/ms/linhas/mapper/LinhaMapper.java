package br.com.fiap.ms.linhas.mapper;

import br.com.fiap.ms.linhas.dto.request.LinhaRequestDTO;
import br.com.fiap.ms.linhas.dto.response.LinhaResponseDTO;
import br.com.fiap.ms.linhas.entities.Linha;
import org.springframework.stereotype.Component;

/** Converte entre a entidade e os DTOs, para que a entidade nunca seja exposta na API. */
@Component
public class LinhaMapper {

    public Linha toEntity(LinhaRequestDTO request) {
        Linha linha = new Linha();
        copiarDados(request, linha);
        return linha;
    }

    public void copiarDados(LinhaRequestDTO request, Linha linha) {
        linha.setNumero(request.numero());
        linha.setNome(request.nome().trim());
        linha.setTipo(request.tipo());
        linha.setCorHex(request.corHex().toUpperCase());
    }

    public LinhaResponseDTO toResponse(Linha linha) {
        return new LinhaResponseDTO(
                linha.getId(),
                linha.getNumero(),
                linha.getNome(),
                linha.getTipo(),
                linha.getCorHex(),
                linha.getStatus(),
                linha.getAtualizadaEm());
    }
}
