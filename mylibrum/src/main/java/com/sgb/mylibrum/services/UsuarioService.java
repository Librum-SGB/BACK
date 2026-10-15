package com.sgb.mylibrum.services;

import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sgb.mylibrum.dtos.request.UsuarioRequestDTO;
import com.sgb.mylibrum.dtos.mapper.UsuarioMapper;
import com.sgb.mylibrum.dtos.response.UsuarioResponseDTO;
import com.sgb.mylibrum.entities.enums.StatusEmprestimo;
import com.sgb.mylibrum.entities.enums.StatusMulta;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.FuncaoUsuario;
import com.sgb.mylibrum.exceptions.ResourceNotFoundException;
import com.sgb.mylibrum.repositories.EmprestimoRepository;
import com.sgb.mylibrum.repositories.HistoricoMultaRepository;
import com.sgb.mylibrum.repositories.UsuarioRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;
import org.springframework.data.jpa.domain.Specification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final EmprestimoRepository emprestimoRepository;
    private final HistoricoMultaRepository historicoMultaRepository;
    private final UsuarioMapper mapper;

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> findAll(
            String nome, String matriculaFuncionario, Boolean ativo, Pageable pageable) {
        String nomeBusca = nome == null || nome.isBlank() ? null : nome.trim();
        String matriculaBusca = normalizarMatricula(matriculaFuncionario);
        Specification<Usuario> filtros = AtivoSpecification.<Usuario>ativo(ativo)
                .and(AtivoSpecification.contemIgnoreCase("nome", nomeBusca))
                .and(AtivoSpecification.<Usuario>igual("matriculaFuncionario", matriculaBusca));
        Page<Usuario> usuarios = repository.findAll(filtros, pageable);
        return usuarios.map(mapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO findById(Long id) {
        return mapper.toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado")));
    }

    @Transactional
    public UsuarioResponseDTO create(UsuarioRequestDTO dto) {
        validarUnicidade(dto, null);
        Usuario entity = mapper.toEntity(dto);
        entity.setEmail(normalizarEmail(dto.getEmail()));
        entity.setMatriculaFuncionario(normalizarMatricula(dto.getMatriculaFuncionario()));
        if (dto.getFuncao() == null) {
            entity.setFuncao(FuncaoUsuario.USUARIO);
        }
        entity.setSenha(passwordEncoder.encode(dto.getSenha()));
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional
    public UsuarioResponseDTO update(Long id, UsuarioRequestDTO dto) {
        Usuario entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        validarUnicidade(dto, id);
        mapper.updateEntity(dto, entity);
        entity.setEmail(normalizarEmail(dto.getEmail()));
        entity.setMatriculaFuncionario(normalizarMatricula(dto.getMatriculaFuncionario()));
        entity.setSenha(passwordEncoder.encode(dto.getSenha()));
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        if (usuario.getFuncao() == FuncaoUsuario.ADMIN) {
            throw new IllegalStateException("Não é possível excluir um usuário com função Admin");
        }
        if (emprestimoRepository.existsByUsuarioIdAndStatusIn(
                id, List.of(StatusEmprestimo.ATIVO, StatusEmprestimo.ATRASADO))) {
            throw new IllegalStateException("Não é possível excluir um usuário com empréstimos ativos");
        }
        usuario.setExcluido(true);
        repository.save(usuario);
    }

    @Transactional
    public UsuarioResponseDTO atualizarBloqueio(Long id, Boolean bloqueado) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        if (!bloqueado && historicoMultaRepository.existsByUsuarioIdAndStatus(id, StatusMulta.PENDENTE)) {
            throw new IllegalStateException("Não é possível desbloquear usuário com multas pendentes");
        }
        usuario.setEstaBloqueado(bloqueado);
        return mapper.toResponseDTO(repository.save(usuario));
    }

    private void validarUnicidade(UsuarioRequestDTO dto, Long usuarioId) {
        String email = normalizarEmail(dto.getEmail());
        boolean emailDuplicado = usuarioId == null
                ? repository.existsByEmailIgnoreCase(email)
                : repository.existsByEmailIgnoreCaseAndIdNot(email, usuarioId);
        if (emailDuplicado) {
            throw new IllegalStateException("Já existe um usuário cadastrado com este e-mail");
        }

        String matricula = normalizarMatricula(dto.getMatriculaFuncionario());
        if (matricula != null) {
            boolean matriculaDuplicada = usuarioId == null
                    ? repository.existsByMatriculaFuncionarioIgnoreCase(matricula)
                    : repository.existsByMatriculaFuncionarioIgnoreCaseAndIdNot(matricula, usuarioId);
            if (matriculaDuplicada) {
                throw new IllegalStateException("Já existe um usuário cadastrado com esta matrícula");
            }
        }
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizarMatricula(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            return null;
        }
        return matricula.trim().toUpperCase(Locale.ROOT);
    }

}