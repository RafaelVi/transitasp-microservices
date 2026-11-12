package br.com.fiap.ms.ocorrencias.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_ocorrencia")
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Id da linha no ms-linhas. Não há chave estrangeira: cada serviço tem o seu banco. */
    @Column(name = "linha_id", nullable = false)
    private Long linhaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoOcorrencia tipo;

    @Column(name = "descricao", nullable = false, length = 255)
    private String descricao;

    @Column(name = "local_ocorrencia", nullable = false, length = 80)
    private String local;

    @Enumerated(EnumType.STRING)
    @Column(name = "impacto", nullable = false, length = 20)
    private ImpactoLinha impacto;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private StatusOcorrencia status;

    @Column(name = "aberta_em", nullable = false)
    private LocalDateTime abertaEm;

    @Column(name = "encerrada_em")
    private LocalDateTime encerradaEm;

    public Ocorrencia() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLinhaId() {
        return linhaId;
    }

    public void setLinhaId(Long linhaId) {
        this.linhaId = linhaId;
    }

    public TipoOcorrencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoOcorrencia tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public ImpactoLinha getImpacto() {
        return impacto;
    }

    public void setImpacto(ImpactoLinha impacto) {
        this.impacto = impacto;
    }

    public StatusOcorrencia getStatus() {
        return status;
    }

    public void setStatus(StatusOcorrencia status) {
        this.status = status;
    }

    public LocalDateTime getAbertaEm() {
        return abertaEm;
    }

    public void setAbertaEm(LocalDateTime abertaEm) {
        this.abertaEm = abertaEm;
    }

    public LocalDateTime getEncerradaEm() {
        return encerradaEm;
    }

    public void setEncerradaEm(LocalDateTime encerradaEm) {
        this.encerradaEm = encerradaEm;
    }
}
