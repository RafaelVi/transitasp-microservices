package br.com.fiap.ms.linhas.repository;

import br.com.fiap.ms.linhas.entities.Linha;
import br.com.fiap.ms.linhas.entities.StatusLinha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LinhaRepository extends JpaRepository<Linha, Long> {

    List<Linha> findAllByOrderByNumeroAsc();

    List<Linha> findByStatusOrderByNumeroAsc(StatusLinha status);

    boolean existsByNumero(Integer numero);

    boolean existsByNumeroAndIdNot(Integer numero, Long id);
}
