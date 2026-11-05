package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.HistoricoMultaRequestDTO;
import com.sgb.mylibrum.dtos.mapper.HistoricoMultaMapper;
import com.sgb.mylibrum.dtos.response.HistoricoMultaResponseDTO;
import com.sgb.mylibrum.entities.Emprestimo;
import com.sgb.mylibrum.entities.HistoricoMulta;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.StatusMulta;
import com.sgb.mylibrum.exceptions.BusinessException;
import com.sgb.mylibrum.exceptions.ResourceNotFoundException;
import com.sgb.mylibrum.repositories.ConfiguracaoRepository;
import com.sgb.mylibrum.repositories.EmprestimoRepository;
import com.sgb.mylibrum.repositories.HistoricoMultaRepository;
import com.sgb.mylibrum.repositories.UsuarioRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class HistoricoMultaService {

    private static final String CONFIG_VALOR_MULTA_POR_DIA = "multa.valor.dia";

    private final HistoricoMultaRepository repository;
    private final EmprestimoRepository emprestimoRepository;
    private final ConfiguracaoRepository configuracaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistoricoMultaMapper mapper;

    @Transactional(readOnly = true)
    public List<HistoricoMultaResponseDTO> findAll(StatusMulta status, Boolean ativo) {
        log.debug("Buscando multas com status={} e filtro de ativo={}", status, ativo);
        List<HistoricoMulta> multas = repository.findAll(AtivoSpecification.<HistoricoMulta>ativo(ativo)
                .and(AtivoSpecification.igual("status", status)));
        return multas.stream().map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<HistoricoMultaResponseDTO> findByUsuarioId(Long usuarioId, Boolean ativo) {
        log.debug("Buscando multas do usuarioId={} com filtro de ativo={}", usuarioId, ativo);
        return repository.findAll(AtivoSpecification.<HistoricoMulta>ativo(ativo)
                        .and(AtivoSpecification.igual("usuario.id", usuarioId))).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public HistoricoMultaResponseDTO findById(Long id) {
        log.debug("Buscando multa com id={}", id);
        return mapper.toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Histórico de multa não encontrado")));
    }

    @Transactional
    public HistoricoMultaResponseDTO create(HistoricoMultaRequestDTO dto) {
        log.info("Criando multa para emprestimoId={}", dto.getEmprestimoId());
        HistoricoMulta entity = mapper.toEntity(dto);
        Emprestimo emprestimo = buscarEmprestimo(dto.getEmprestimoId());
        entity.setEmprestimo(emprestimo);
        entity.setUsuario(emprestimo.getUsuario());
        entity.setStatus(StatusMulta.PENDENTE);
        HistoricoMulta salva = repository.save(entity);
        atualizarBloqueio(emprestimo.getUsuario());
        log.info("Multa criada: multaId={}, emprestimoId={}, usuarioId={}, status={}", salva.getId(), emprestimo.getId(), emprestimo.getUsuario().getId(), salva.getStatus());
        return mapper.toResponseDTO(salva);
    }

    @Transactional
    public HistoricoMultaResponseDTO update(Long id, HistoricoMultaRequestDTO dto) {
        log.info("Atualizando multa: multaId={}", id);
        HistoricoMulta entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Histórico de multa não encontrado"));
        Usuario usuarioAnterior = entity.getUsuario();
        mapper.updateEntity(dto, entity);
        if (dto.getEmprestimoId() != null) {
            Emprestimo emprestimo = buscarEmprestimo(dto.getEmprestimoId());
            entity.setEmprestimo(emprestimo);
            entity.setUsuario(emprestimo.getUsuario());
        }
        HistoricoMulta salva = repository.save(entity);
        atualizarBloqueio(usuarioAnterior);
        if (!usuarioAnterior.getId().equals(entity.getUsuario().getId())) {
            atualizarBloqueio(entity.getUsuario());
        }
        log.info("Multa atualizada: multaId={}, emprestimoId={}, usuarioId={}, status={}", salva.getId(), salva.getEmprestimo().getId(), salva.getUsuario().getId(), salva.getStatus());
        return mapper.toResponseDTO(salva);
    }

    @Transactional
    public void delete(Long id) {
        HistoricoMulta multa = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Histórico de multa não encontrado"));
        Usuario usuario = multa.getUsuario();
        multa.setExcluido(true);
        repository.save(multa);
        atualizarBloqueio(usuario);
        log.info("Multa excluida: multaId={}, usuarioId={}", id, usuario.getId());
    }

    @Transactional
    public HistoricoMultaResponseDTO registrarPagamento(Long id) {
        HistoricoMulta multa = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Histórico de multa não encontrado"));
        if (multa.getStatus() == StatusMulta.PAGA) {
            log.warn("Pagamento rejeitado: multa ja paga, multaId={}", id);
            throw new BusinessException("Esta multa já foi paga");
        }

        multa.setStatus(StatusMulta.PAGA);
        multa.setDataPagamento(LocalDate.now());
        HistoricoMulta salva = repository.save(multa);
        atualizarBloqueio(multa.getUsuario());
        log.info("Multa paga: multaId={}, usuarioId={}", salva.getId(), multa.getUsuario().getId());
        return mapper.toResponseDTO(salva);
    }

    @Scheduled(cron = "${mylibrum.multas.gerar-cron:0 5 0 * * *}")
    @Transactional
    public void gerarMultasPorAtraso() {
        log.debug("Iniciando geracao automatica de multas por atraso");
        LocalDate hoje = LocalDate.now();
        List<Emprestimo> emprestimosAtrasados = emprestimoRepository
                .findByStatusInAndDataDevolucaoPrevistaBefore(
                        List.of(com.sgb.mylibrum.entities.enums.StatusEmprestimo.ATIVO,
                                com.sgb.mylibrum.entities.enums.StatusEmprestimo.ATRASADO),
                        hoje);

        for (Emprestimo emprestimo : emprestimosAtrasados) {
            long diasAtraso = ChronoUnit.DAYS.between(emprestimo.getDataDevolucaoPrevista(), hoje);
            if (diasAtraso <= 0) {
                continue;
            }

            BigDecimal valorDiario = buscarValorMultaDiaria(emprestimo.getUsuario().getFilial().getId());
            List<HistoricoMulta> multas = repository.findByEmprestimoId(emprestimo.getId());
            int diasJaFaturados = multas.stream()
                    .map(HistoricoMulta::getDiasAtraso)
                    .filter(dias -> dias != null && dias > 0)
                    .mapToInt(Integer::intValue)
                    .sum();
            int novosDias = Math.toIntExact(Math.max(0, diasAtraso - diasJaFaturados));

            if (emprestimo.getStatus() == com.sgb.mylibrum.entities.enums.StatusEmprestimo.ATIVO) {
                log.info("Emprestimo marcado como atrasado durante geracao de multa: emprestimoId={}", emprestimo.getId());
                emprestimo.setStatus(com.sgb.mylibrum.entities.enums.StatusEmprestimo.ATRASADO);
            }

            if (novosDias > 0) {
                List<HistoricoMulta> pendentes = multas.stream()
                        .filter(multa -> multa.getStatus() == StatusMulta.PENDENTE)
                        .toList();
                BigDecimal valorIncremental = valorDiario.multiply(BigDecimal.valueOf(novosDias));
                if (pendentes.isEmpty()) {
                    HistoricoMulta multa = new HistoricoMulta();
                    multa.setEmprestimo(emprestimo);
                    multa.setUsuario(emprestimo.getUsuario());
                    multa.setDiasAtraso(novosDias);
                    multa.setValor(valorIncremental);
                    multa.setStatus(StatusMulta.PENDENTE);
                    HistoricoMulta salva = repository.save(multa);
                    log.info("Multa gerada por atraso: multaId={}, emprestimoId={}, diasAtraso={}", salva.getId(), emprestimo.getId(), novosDias);
                } else {
                    HistoricoMulta pendente = pendentes.get(0);
                    pendente.setDiasAtraso(pendente.getDiasAtraso() + novosDias);
                    pendente.setValor(pendente.getValor().add(valorIncremental));
                    repository.save(pendente);
                    log.info("Multa atualizada por atraso: multaId={}, emprestimoId={}, diasAdicionados={}", pendente.getId(), emprestimo.getId(), novosDias);
                }
            }

            emprestimoRepository.save(emprestimo);
            atualizarBloqueio(emprestimo.getUsuario());
        }
    }

    private BigDecimal buscarValorMultaDiaria(Long filialId) {
        String valor = configuracaoRepository.findByFilialIdAndChave(filialId, CONFIG_VALOR_MULTA_POR_DIA)
                .orElseThrow(() -> new IllegalStateException(
                        "Configure '" + CONFIG_VALOR_MULTA_POR_DIA + "' para a filial do usuário"))
                .getValor();
        try {
            BigDecimal valorDiario = new BigDecimal(valor).setScale(2, RoundingMode.HALF_UP);
            if (valorDiario.signum() <= 0) {
                throw new IllegalStateException("O valor diário da multa deve ser maior que zero");
            }
            return valorDiario;
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("O valor diário da multa configurado deve ser numérico", ex);
        }
    }

    private void atualizarBloqueio(Usuario usuario) {
        boolean possuiMultasPendentes = repository.existsByUsuarioIdAndStatus(
                usuario.getId(), StatusMulta.PENDENTE);
        if (!Boolean.valueOf(possuiMultasPendentes).equals(usuario.getEstaBloqueado())) {
            usuario.setEstaBloqueado(possuiMultasPendentes);
            usuarioRepository.save(usuario);
            log.info("Estado de bloqueio atualizado: usuarioId={}, bloqueado={}", usuario.getId(), possuiMultasPendentes);
        }
    }

    private Emprestimo buscarEmprestimo(Long emprestimoId) {
        return emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado"));
    }

}