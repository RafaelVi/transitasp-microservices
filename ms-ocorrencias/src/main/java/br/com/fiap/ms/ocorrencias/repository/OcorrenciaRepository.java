package br.com.fiap.ms.ocorrencias.repository;

import br.com.fiap.ms.ocorrencias.entities.Ocorrencia;
import br.com.fiap.ms.ocorrencias.entities.StatusOcorrencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {

    List<Ocorrencia> findAllByOrderByAbertaEmDesc();

    List<Ocorrencia> findByStatusOrderByAbertaEmDesc(StatusOcorrencia status);

    List<Ocorrencia> findByLinhaIdOrderByAbertaEmDesc(Long linhaId);

    List<Ocorrencia> findByLinhaIdAndStatusOrderByAbertaEmDesc(Long linhaId, StatusOcorrencia status);
}
