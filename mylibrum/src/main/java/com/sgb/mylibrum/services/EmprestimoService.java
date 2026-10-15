package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.EmprestimoRequestDTO;
import com.sgb.mylibrum.dtos.mapper.EmprestimoMapper;
import com.sgb.mylibrum.dtos.response.EmprestimoResponseDTO;
import com.sgb.mylibrum.entities.Configuracao;
import com.sgb.mylibrum.entities.Emprestimo;
import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.StatusEmprestimo;
import com.sgb.mylibrum.entities.enums.StatusExemplar;
import com.sgb.mylibrum.exceptions.ResourceNotFoundException;
import com.sgb.mylibrum.repositories.ConfiguracaoRepository;
import com.sgb.mylibrum.repositories.EmprestimoRepository;
import com.sgb.mylibrum.repositories.ExemplarRepository;
import com.sgb.mylibrum.repositories.UsuarioRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private static final String CONFIG_LIMITE_RENOVACOES = "emprestimo.renovacoes.max";

    private final EmprestimoRepository repository;
    private final ExemplarRepository exemplarRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConfiguracaoRepository configuracaoRepository;
    private final EmprestimoMapper mapper;

    @Transactional(readOnly = true)
    public List<EmprestimoResponseDTO> findAll(Boolean ativo) {
        return repository.findAll(AtivoSpecification.ativo(ativo)).stream()
                .map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<EmprestimoResponseDTO> findByUsuarioId(Long usuarioId, Boolean ativo) {
        return repository.findAll(AtivoSpecification.<Emprestimo>ativo(ativo)
                        .and(AtivoSpecification.igual("usuario.id", usuarioId))).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmprestimoResponseDTO findById(Long id) {
        return mapper.toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado")));
    }

    @Transactional
    public EmprestimoResponseDTO create(EmprestimoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        if (Boolean.TRUE.equals(usuario.getEstaBloqueado())) {
            throw new IllegalStateException("Usuário bloqueado não pode realizar empréstimos");
        }

        Exemplar exemplar = exemplarRepository.findByIdForUpdate(dto.getExemplarId())
                .orElseThrow(() -> new ResourceNotFoundException("Exemplar não encontrado"));
        if (exemplar.getStatus() != StatusExemplar.DISPONIVEL) {
            throw new IllegalStateException("O exemplar não está disponível para empréstimo");
        }

        Usuario responsavel = usuarioRepository.findById(dto.getUsuarioResponsavelId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário responsável não encontrado"));

        Emprestimo entity = mapper.toEntity(dto);
        entity.setUsuario(usuario);
        entity.setExemplar(exemplar);
        entity.setUsuarioResponsavel(responsavel);
        entity.setDataSaida(OffsetDateTime.now());
        entity.setStatus(StatusEmprestimo.ATIVO);
        entity.setRenovacoesContagem(0);

        exemplar.setStatus(StatusExemplar.EMPRESTADO);
        exemplarRepository.save(exemplar);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional
    public EmprestimoResponseDTO update(Long id, EmprestimoRequestDTO dto) {
        buscarEmprestimo(id);
        throw new IllegalStateException(
                "Empréstimos não podem ser alterados diretamente; use as operações de devolução ou renovação");
    }

    @Transactional
    public EmprestimoResponseDTO devolver(Long id) {
        Emprestimo emprestimo = buscarEmprestimo(id);
        if (emprestimo.getStatus() == StatusEmprestimo.DEVOLVIDO) {
            throw new IllegalStateException("Este empréstimo já foi devolvido");
        }

        emprestimo.setStatus(StatusEmprestimo.DEVOLVIDO);
        emprestimo.setDataDevolucaoEfetivada(OffsetDateTime.now());
        Exemplar exemplar = exemplarRepository.findByIdForUpdate(emprestimo.getExemplar().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Exemplar não encontrado"));
        exemplar.setStatus(StatusExemplar.DISPONIVEL);
        exemplarRepository.save(exemplar);
        return mapper.toResponseDTO(repository.save(emprestimo));
    }

    @Transactional
    public EmprestimoResponseDTO renovar(Long id) {
        Emprestimo emprestimo = buscarEmprestimo(id);
        if (emprestimo.getStatus() != StatusEmprestimo.ATIVO
                || emprestimo.getDataDevolucaoPrevista().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Somente empréstimos ativos e dentro do prazo podem ser renovados");
        }
        if (Boolean.TRUE.equals(emprestimo.getUsuario().getEstaBloqueado())) {
            throw new IllegalStateException("Usuário bloqueado não pode renovar empréstimos");
        }

        int limiteRenovacoes = buscarLimiteRenovacoes(emprestimo.getUsuario().getFilial().getId());
        int renovacoes = emprestimo.getRenovacoesContagem() == null ? 0 : emprestimo.getRenovacoesContagem();
        if (renovacoes >= limiteRenovacoes) {
            throw new IllegalStateException("O limite de renovações deste empréstimo foi atingido");
        }

        if (emprestimo.getDataSaida() == null) {
            throw new IllegalStateException("Não foi possível calcular o prazo original do empréstimo");
        }
        long prazoTotal = ChronoUnit.DAYS.between(
                emprestimo.getDataSaida().toLocalDate(), emprestimo.getDataDevolucaoPrevista());
        if (prazoTotal <= 0 || prazoTotal % (renovacoes + 1) != 0) {
            throw new IllegalStateException("O prazo original do empréstimo é inválido");
        }

        long prazoOriginal = prazoTotal / (renovacoes + 1);
        emprestimo.setDataDevolucaoPrevista(emprestimo.getDataDevolucaoPrevista().plusDays(prazoOriginal));
        emprestimo.setRenovacoesContagem(renovacoes + 1);
        return mapper.toResponseDTO(repository.save(emprestimo));
    }

    @Scheduled(cron = "${mylibrum.emprestimos.marcar-atrasados-cron:0 0 0 * * *}")
    @Transactional
    public void marcarEmprestimosAtrasados() {
        List<Emprestimo> atrasados = repository.findByStatusInAndDataDevolucaoPrevistaBefore(
                List.of(StatusEmprestimo.ATIVO, StatusEmprestimo.ATRASADO), LocalDate.now());
        atrasados.forEach(emprestimo -> emprestimo.setStatus(StatusEmprestimo.ATRASADO));
        repository.saveAll(atrasados);
    }

    @Transactional
    public void delete(Long id) {
        Emprestimo emprestimo = buscarEmprestimo(id);
        if (emprestimo.getStatus() != StatusEmprestimo.DEVOLVIDO) {
            throw new IllegalStateException("Somente empréstimos devolvidos podem ser removidos");
        }
        emprestimo.setExcluido(true);
        repository.save(emprestimo);
    }

    private Emprestimo buscarEmprestimo(Long id) {
        return repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado"));
    }

    private int buscarLimiteRenovacoes(Long filialId) {
        Configuracao configuracao = configuracaoRepository
                .findByFilialIdAndChave(filialId, CONFIG_LIMITE_RENOVACOES)
                .orElseThrow(() -> new IllegalStateException(
                        "Configure '" + CONFIG_LIMITE_RENOVACOES + "' para a filial do usuário"));
        try {
            int limite = Integer.parseInt(configuracao.getValor());
            if (limite < 0) {
                throw new IllegalStateException("O limite de renovações configurado não pode ser negativo");
            }
            return limite;
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("O limite de renovações configurado deve ser um número inteiro", ex);
        }
    }

}