package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.HistoricoMultaRequestDTO;
import com.sgb.mylibrum.dtos.response.HistoricoMultaResponseDTO;
import com.sgb.mylibrum.entities.Emprestimo;
import com.sgb.mylibrum.entities.HistoricoMulta;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.StatusMulta;
import com.sgb.mylibrum.exceptions.ResourceNotFoundException;
import com.sgb.mylibrum.repositories.ConfiguracaoRepository;
import com.sgb.mylibrum.repositories.EmprestimoRepository;
import com.sgb.mylibrum.repositories.HistoricoMultaRepository;
import com.sgb.mylibrum.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoricoMultaService {

    private static final String CONFIG_VALOR_MULTA_POR_DIA = "multa.valor.dia";

    private final HistoricoMultaRepository repository;
    private final EmprestimoRepository emprestimoRepository;
    private final ConfiguracaoRepository configuracaoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<HistoricoMultaResponseDTO> findAll(StatusMulta status) {
        List<HistoricoMulta> multas = status == null
                ? repository.findAll()
                : repository.findByStatus(status);
        return multas.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HistoricoMultaResponseDTO> findByUsuarioId(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HistoricoMultaResponseDTO findById(Long id) {
        return toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Histórico de multa não encontrado")));
    }

    @Transactional
    public HistoricoMultaResponseDTO create(HistoricoMultaRequestDTO dto) {
        HistoricoMulta entity = new HistoricoMulta();
        BeanUtils.copyProperties(dto, entity);
        Emprestimo emprestimo = buscarEmprestimo(dto.getEmprestimoId());
        entity.setEmprestimo(emprestimo);
        entity.setUsuario(emprestimo.getUsuario());
        entity.setStatus(StatusMulta.PENDENTE);
        HistoricoMulta salva = repository.save(entity);
        atualizarBloqueio(emprestimo.getUsuario());
        return toResponseDTO(salva);
    }

    @Transactional
    public HistoricoMultaResponseDTO update(Long id, HistoricoMultaRequestDTO dto) {
        HistoricoMulta entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Histórico de multa não encontrado"));
        Usuario usuarioAnterior = entity.getUsuario();
        BeanUtils.copyProperties(dto, entity, "id", "status", "dataGeracao", "dataPagamento",
                "dataCriacao", "dataUltimaAtualizacao");
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
        return toResponseDTO(salva);
    }

    @Transactional
    public void delete(Long id) {
        HistoricoMulta multa = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Histórico de multa não encontrado"));
        Usuario usuario = multa.getUsuario();
        repository.delete(multa);
        atualizarBloqueio(usuario);
    }

    @Transactional
    public HistoricoMultaResponseDTO registrarPagamento(Long id) {
        HistoricoMulta multa = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Histórico de multa não encontrado"));
        if (multa.getStatus() == StatusMulta.PAGA) {
            throw new IllegalStateException("Esta multa já foi paga");
        }

        multa.setStatus(StatusMulta.PAGA);
        multa.setDataPagamento(LocalDate.now());
        HistoricoMulta salva = repository.save(multa);
        atualizarBloqueio(multa.getUsuario());
        return toResponseDTO(salva);
    }

    @Scheduled(cron = "${mylibrum.multas.gerar-cron:0 5 0 * * *}")
    @Transactional
    public void gerarMultasPorAtraso() {
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
                    repository.save(multa);
                } else {
                    HistoricoMulta pendente = pendentes.get(0);
                    pendente.setDiasAtraso(pendente.getDiasAtraso() + novosDias);
                    pendente.setValor(pendente.getValor().add(valorIncremental));
                    repository.save(pendente);
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
        }
    }

    private Emprestimo buscarEmprestimo(Long emprestimoId) {
        return emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado"));
    }

    private HistoricoMultaResponseDTO toResponseDTO(HistoricoMulta entity) {
        HistoricoMultaResponseDTO dto = new HistoricoMultaResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getEmprestimo() != null) dto.setEmprestimoId(entity.getEmprestimo().getId());
        if (entity.getUsuario() != null) dto.setUsuarioId(entity.getUsuario().getId());
        return dto;
    }
}