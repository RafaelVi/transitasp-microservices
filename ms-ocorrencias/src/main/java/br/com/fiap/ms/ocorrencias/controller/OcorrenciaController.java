package br.com.fiap.ms.ocorrencias.controller;

import br.com.fiap.ms.ocorrencias.dto.request.OcorrenciaRequestDTO;
import br.com.fiap.ms.ocorrencias.dto.request.OcorrenciaUpdateRequestDTO;
import br.com.fiap.ms.ocorrencias.dto.response.OcorrenciaDetalheResponseDTO;
import br.com.fiap.ms.ocorrencias.dto.response.OcorrenciaResponseDTO;
import br.com.fiap.ms.ocorrencias.entities.StatusOcorrencia;
import br.com.fiap.ms.ocorrencias.service.OcorrenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ocorrencias")
@Tag(name = "Ocorrências", description = "Ocorrências nas linhas; abrir, alterar ou encerrar muda o status da linha no ms-linhas")
public class OcorrenciaController {

    private final OcorrenciaService service;

    public OcorrenciaController(OcorrenciaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista as ocorrências", description = "Opcionalmente filtra por status e/ou por linha.")
    public ResponseEntity<List<OcorrenciaResponseDTO>> listar(
            @RequestParam(name = "status", required = false) StatusOcorrencia status,
            @RequestParam(name = "linhaId", required = false) Long linhaId) {
        return ResponseEntity.ok(service.listar(status, linhaId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma ocorrência pelo id",
            description = "Traz também a situação atual da linha, consultada no ms-linhas. "
                    + "Se ele estiver fora do ar, a ocorrência vem com linhaDisponivel=false (fallback).")
    public ResponseEntity<OcorrenciaDetalheResponseDTO> buscarPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Abre uma ocorrência",
            description = "Valida a linha no ms-linhas e altera o status dela conforme o impacto informado.")
    public ResponseEntity<OcorrenciaDetalheResponseDTO> criar(@Valid @RequestBody OcorrenciaRequestDTO request) {
        OcorrenciaDetalheResponseDTO criada = service.criar(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criada.ocorrencia().id())
                .toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma ocorrência aberta",
            description = "Se o impacto mudar, o status da linha é recalculado no ms-linhas.")
    public ResponseEntity<OcorrenciaDetalheResponseDTO> atualizar(@PathVariable("id") Long id,
                                                               @Valid @RequestBody OcorrenciaUpdateRequestDTO request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @PutMapping("/{id}/encerrar")
    @Operation(summary = "Encerra uma ocorrência",
            description = "A linha volta para NORMAL no ms-linhas, ou para o impacto mais grave entre as ocorrências que continuam abertas.")
    public ResponseEntity<OcorrenciaDetalheResponseDTO> encerrar(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.encerrar(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma ocorrência",
            description = "Se ela estava aberta, o status da linha é recalculado no ms-linhas.")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
