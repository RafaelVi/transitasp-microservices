package br.com.fiap.ms.linhas.controller;

import br.com.fiap.ms.linhas.dto.request.AlterarStatusRequestDTO;
import br.com.fiap.ms.linhas.dto.request.LinhaRequestDTO;
import br.com.fiap.ms.linhas.dto.response.LinhaResponseDTO;
import br.com.fiap.ms.linhas.entities.StatusLinha;
import br.com.fiap.ms.linhas.service.LinhaService;
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
@RequestMapping("/api/v1/linhas")
@Tag(name = "Linhas", description = "Cadastro e status das linhas de metrô e trem")
public class LinhaController {

    private final LinhaService service;

    public LinhaController(LinhaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista as linhas", description = "Opcionalmente filtra pelo status.")
    public ResponseEntity<List<LinhaResponseDTO>> listar(
            @RequestParam(name = "status", required = false) StatusLinha status) {
        return ResponseEntity.ok(service.listar(status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma linha pelo id")
    public ResponseEntity<LinhaResponseDTO> buscarPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastra uma linha", description = "A linha é criada com status NORMAL.")
    public ResponseEntity<LinhaResponseDTO> criar(@Valid @RequestBody LinhaRequestDTO request) {
        LinhaResponseDTO criada = service.criar(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criada.id())
                .toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza os dados de uma linha")
    public ResponseEntity<LinhaResponseDTO> atualizar(@PathVariable("id") Long id,
                                                   @Valid @RequestBody LinhaRequestDTO request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma linha", description = "Só é permitido quando a linha está com status NORMAL.")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Altera o status da linha",
            description = "Endpoint usado pelo ms-ocorrencias, via OpenFeign, quando uma ocorrência é aberta, alterada ou encerrada.")
    public ResponseEntity<LinhaResponseDTO> alterarStatus(@PathVariable("id") Long id,
                                                       @Valid @RequestBody AlterarStatusRequestDTO request) {
        return ResponseEntity.ok(service.alterarStatus(id, request));
    }
}
